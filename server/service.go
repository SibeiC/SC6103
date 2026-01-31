package main

import (
	"fmt"
	"math/rand"
	"net"
	"time"

	"sc6103-project/proto"
)

// Account represents a bank account
type Account struct {
	AccountNumber uint32
	Name          string
	Password      string
	Currency      uint8
	Balance       float64
}

// MonitorClient represents a client registered for updates
type MonitorClient struct {
	Address    *net.UDPAddr
	ExpiryTime time.Time
}

// Service handles banking operations and state
type Service struct {
	accounts map[uint32]*Account
	monitors []MonitorClient
	udpConn  *net.UDPConn // Needed for sending callbacks
	lossRate int          // For callback packet loss simulation
}

func NewService(conn *net.UDPConn, lossRate int) *Service {
	return &Service{
		accounts: make(map[uint32]*Account),
		monitors: make([]MonitorClient, 0),
		udpConn:  conn,
		lossRate: lossRate,
	}
}

// Helper to check password
func (s *Service) checkAuth(accID uint32, pass string, name string) (*Account, error) {
	acc, exists := s.accounts[accID]
	if !exists {
		return nil, fmt.Errorf("account %d does not exist", accID)
	}
	if acc.Name != name {
		return nil, fmt.Errorf("name mismatch")
	}
	if acc.Password != pass {
		return nil, fmt.Errorf("incorrect password")
	}
	return acc, nil
}

// OpOpen: Create new account
// Request Body: [Name Len][Name][Pass Len][Pass][Currency: 1 byte][Initial Balance: 8 bytes]
// Reply: [AccountID: 4 bytes]
func (s *Service) OpenAccount(body []byte) ([]byte, error) {
	req, err := proto.UnmarshalOpenAccountRequest(body)
	if err != nil {
		return nil, err
	}

	name := req.Name
	pass := req.Password
	currency := req.Currency
	balance := req.Balance

	// Generate ID
	id := rand.Uint32()
	for _, exists := s.accounts[id]; exists; _, exists = s.accounts[id] {
		id = rand.Uint32()
	}

	acc := &Account{
		AccountNumber: id,
		Name:          name,
		Password:      pass,
		Currency:      currency,
		Balance:       balance,
	}
	s.accounts[id] = acc

	s.notifyMonitors(acc)

	return proto.PutUint32(nil, id), nil
}

// OpClose: Close account
// Request Body: [Name Len][Name][Pass Len][Pass][AccountID: 4 bytes]
// Reply: [Message String]
func (s *Service) CloseAccount(body []byte) ([]byte, error) {
	req, err := proto.UnmarshalAuthRequest(body)
	if err != nil {
		return nil, err
	}

	acc, err := s.checkAuth(req.AccountID, req.Password, req.Name)
	if err != nil {
		return nil, err
	}

	// Capture details for notification before deleting
	accCopy := *acc
	delete(s.accounts, req.AccountID)

	s.notifyMonitors(&accCopy)

	return proto.PutString(nil, fmt.Sprintf("Account %d closed successfully", req.AccountID)), nil
}

// OpDeposit: Deposit money
// Request Body: [Name Len][Name][Pass Len][Pass][AccountID: 4 bytes][Amount: 8 bytes]
// Reply: [New Balance: 8 bytes]
func (s *Service) Deposit(body []byte) ([]byte, error) {
	return s.modifyBalance(body, 1.0)
}

// OpWithdraw: Withdraw money
// Request Body: Same as Deposit
// Reply: [New Balance: 8 bytes]
func (s *Service) Withdraw(body []byte) ([]byte, error) {
	return s.modifyBalance(body, -1.0)
}

func (s *Service) modifyBalance(body []byte, multiplier float64) ([]byte, error) {
	req, err := proto.UnmarshalTransactionRequest(body)
	if err != nil {
		return nil, err
	}

	name := req.Name
	pass := req.Password
	accID := req.AccountID
	amount := req.Amount

	acc, err := s.checkAuth(accID, pass, name)
	if err != nil {
		return nil, err
	}

	if multiplier < 0 && acc.Balance < amount {
		return nil, fmt.Errorf("insufficient funds")
	}

	acc.Balance += amount * multiplier
	s.notifyMonitors(acc)

	return proto.PutFloat64(nil, acc.Balance), nil
}

// OpMonitor: Register for updates
// Request Body: [Duration Seconds: 4 bytes] (using uint32 for seconds)
// Reply: [Message String]
func (s *Service) RegisterMonitor(body []byte, addr *net.UDPAddr) ([]byte, error) {
	req, err := proto.UnmarshalMonitorRequest(body)
	if err != nil {
		return nil, err
	}
	durationSec := req.DurationSec

	expiry := time.Now().Add(time.Duration(durationSec) * time.Second)
	s.monitors = append(s.monitors, MonitorClient{
		Address:    addr,
		ExpiryTime: expiry,
	})

	return proto.PutString(nil, fmt.Sprintf("Subscribed for %d seconds", durationSec)), nil
}

// OpCheckBalance: Idempotent operation
// Request Body: [Name Len][Name][Pass Len][Pass][AccountID: 4 bytes]
// Reply: [Balance: 8 bytes]
func (s *Service) CheckBalance(body []byte) ([]byte, error) {
	req, err := proto.UnmarshalAuthRequest(body)
	if err != nil {
		return nil, err
	}

	acc, err := s.checkAuth(req.AccountID, req.Password, req.Name)
	if err != nil {
		return nil, err
	}

	return proto.PutFloat64(nil, acc.Balance), nil
}

// OpTransfer: Transfer money
// Reply: [Sender New Balance: 8 bytes]
func (s *Service) Transfer(body []byte) ([]byte, error) {
	req, err := proto.UnmarshalTransferRequest(body)
	if err != nil {
		return nil, err
	}

	sender, err := s.checkAuth(req.SenderAccountID, req.Password, req.Name)
	if err != nil {
		return nil, err
	}

	receiver, exists := s.accounts[req.ReceiverAccountID]
	if !exists {
		return nil, fmt.Errorf("receiver account %d does not exist", req.ReceiverAccountID)
	}

	if sender.Balance < req.Amount {
		return nil, fmt.Errorf("insufficient funds")
	}

	sender.Balance -= req.Amount
	receiver.Balance += req.Amount

	s.notifyMonitors(sender)
	s.notifyMonitors(receiver)

	return proto.PutFloat64(nil, sender.Balance), nil
}

// notifyMonitors sends an update to all active monitors
// Callback Body Format: [AccountID: 4 bytes][UpdatedBalance: 8 bytes] (Simplified for this example)
func (s *Service) notifyMonitors(acc *Account) {
	now := time.Now()
	activeMonitors := make([]MonitorClient, 0)

	for _, m := range s.monitors {
		if now.Before(m.ExpiryTime) {
			activeMonitors = append(activeMonitors, m)

			// Simple callback payload
			payload := proto.PutUint32(nil, acc.AccountNumber)
			payload = proto.PutFloat64(payload, acc.Balance)

			pkt := proto.Packet{
				MessageType: proto.MsgCallback,
				RequestID:   0, // Callbacks don't have req ID
				Operation:   0,
				Body:        payload,
			}
			data := proto.MarshalPacket(pkt)

			// Simulate Outbound Loss for Callback
			if rand.Intn(100) >= s.lossRate {
				if s.udpConn != nil {
					s.udpConn.WriteToUDP(data, m.Address)
				}
			}
		}
	}
	s.monitors = activeMonitors // Prune expired
}
