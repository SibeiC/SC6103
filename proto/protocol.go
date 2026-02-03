package proto

import (
	"encoding/binary"
	"errors"
	"math"
)

// Constants for Message Types
const (
	MsgRequest  uint8 = 0
	MsgReply    uint8 = 1
	MsgError    uint8 = 2
	MsgCallback uint8 = 3
)

// Constants for Operations
const (
	OpOpen         uint8 = 1
	OpClose        uint8 = 2
	OpDeposit      uint8 = 3
	OpWithdraw     uint8 = 4
	OpMonitor      uint8 = 5
	OpCheckBalance uint8 = 6 // Idempotent
	OpTransfer     uint8 = 7 // Non-Idempotent
)

// Constants for Currencies
const (
	CurrencyUSD uint8 = 0
	CurrencySGD uint8 = 1
	CurrencyEUR uint8 = 2
	CurrencyGBP uint8 = 3
	CurrencyCNY uint8 = 4
)

// OpName maps operation ID to string
func OpName(op uint8) string {
	switch op {
	case OpOpen:
		return "OpOpen"
	case OpClose:
		return "OpClose"
	case OpDeposit:
		return "OpDeposit"
	case OpWithdraw:
		return "OpWithdraw"
	case OpMonitor:
		return "OpMonitor"
	case OpCheckBalance:
		return "OpCheckBalance"
	case OpTransfer:
		return "OpTransfer"
	default:
		return "UnknownOp"
	}
}

// MsgTypeName maps message type ID to string
func MsgTypeName(msgType uint8) string {
	switch msgType {
	case MsgRequest:
		return "Request"
	case MsgReply:
		return "Reply"
	case MsgError:
		return "Error"
	case MsgCallback:
		return "Callback"
	default:
		return "UnknownMsg"
	}
}

// Packet represents the parsed message structure
type Packet struct {
	MessageType uint8
	RequestID   uint32
	Operation   uint8
	Body        []byte
}

// MarshalPacket converts a Packet struct to a byte slice
func MarshalPacket(p Packet) []byte {
	buf := make([]byte, 6+len(p.Body))
	buf[0] = p.MessageType
	binary.BigEndian.PutUint32(buf[1:5], p.RequestID)
	buf[5] = p.Operation
	copy(buf[6:], p.Body)
	return buf
}

// UnmarshalPacket parses a byte slice into a Packet struct
func UnmarshalPacket(data []byte) (Packet, error) {
	if len(data) < 6 {
		return Packet{}, errors.New("packet too short")
	}
	return Packet{
		MessageType: data[0],
		RequestID:   binary.BigEndian.Uint32(data[1:5]),
		Operation:   data[5],
		Body:        data[6:],
	}, nil
}

// --- Normalized Request Structs ---

// OpenAccountRequest
type OpenAccountRequest struct {
	Name     string
	Password string
	Currency uint8
	Balance  float64
}

func (r OpenAccountRequest) Marshal() []byte {
	var buf []byte
	buf = PutString(buf, r.Name)
	buf = PutString(buf, r.Password)
	buf = append(buf, r.Currency)
	buf = PutFloat64(buf, r.Balance)
	return buf
}

func UnmarshalOpenAccountRequest(data []byte) (OpenAccountRequest, error) {
	var r OpenAccountRequest
	offset := 0

	name, n, err := GetString(data[offset:])
	if err != nil {
		return r, err
	}
	r.Name = name
	offset += n

	pass, n, err := GetString(data[offset:])
	if err != nil {
		return r, err
	}
	r.Password = pass
	offset += n

	if len(data) < offset+1 {
		return r, errors.New("buffer too short for currency")
	}
	r.Currency = data[offset]
	offset++

	bal, _, err := GetFloat64(data[offset:])
	if err != nil {
		return r, err
	}
	r.Balance = bal

	return r, nil
}

// AuthRequest (Used for Close, CheckBalance)
type AuthRequest struct {
	Name      string
	Password  string
	AccountID uint32
}

func (r AuthRequest) Marshal() []byte {
	var buf []byte
	buf = PutString(buf, r.Name)
	buf = PutString(buf, r.Password)
	buf = PutUint32(buf, r.AccountID)
	return buf
}

func UnmarshalAuthRequest(data []byte) (AuthRequest, error) {
	var r AuthRequest
	offset := 0

	name, n, err := GetString(data[offset:])
	if err != nil {
		return r, err
	}
	r.Name = name
	offset += n

	pass, n, err := GetString(data[offset:])
	if err != nil {
		return r, err
	}
	r.Password = pass
	offset += n

	id, _, err := GetUint32(data[offset:])
	if err != nil {
		return r, err
	}
	r.AccountID = id

	return r, nil
}

// TransactionRequest (Used for Deposit, Withdraw)
type TransactionRequest struct {
	Name      string
	Password  string
	AccountID uint32
	Amount    float64
}

func (r TransactionRequest) Marshal() []byte {
	var buf []byte
	buf = PutString(buf, r.Name)
	buf = PutString(buf, r.Password)
	buf = PutUint32(buf, r.AccountID)
	buf = PutFloat64(buf, r.Amount)
	return buf
}

func UnmarshalTransactionRequest(data []byte) (TransactionRequest, error) {
	var r TransactionRequest
	offset := 0

	name, n, err := GetString(data[offset:])
	if err != nil {
		return r, err
	}
	r.Name = name
	offset += n

	pass, n, err := GetString(data[offset:])
	if err != nil {
		return r, err
	}
	r.Password = pass
	offset += n

	id, n, err := GetUint32(data[offset:])
	if err != nil {
		return r, err
	}
	r.AccountID = id
	offset += n

	amt, _, err := GetFloat64(data[offset:])
	if err != nil {
		return r, err
	}
	r.Amount = amt

	return r, nil
}

// MonitorRequest
type MonitorRequest struct {
	DurationSec uint32
}

func (r MonitorRequest) Marshal() []byte {
	return PutUint32(nil, r.DurationSec)
}

func UnmarshalMonitorRequest(data []byte) (MonitorRequest, error) {
	sec, _, err := GetUint32(data)
	return MonitorRequest{DurationSec: sec}, err
}

// TransferRequest
type TransferRequest struct {
	Name              string
	Password          string
	SenderAccountID   uint32
	ReceiverAccountID uint32
	Amount            float64
}

func (r TransferRequest) Marshal() []byte {
	var buf []byte
	buf = PutString(buf, r.Name)
	buf = PutString(buf, r.Password)
	buf = PutUint32(buf, r.SenderAccountID)
	buf = PutUint32(buf, r.ReceiverAccountID)
	buf = PutFloat64(buf, r.Amount)
	return buf
}

func UnmarshalTransferRequest(data []byte) (TransferRequest, error) {
	var r TransferRequest
	offset := 0

	name, n, err := GetString(data[offset:])
	if err != nil {
		return r, err
	}
	r.Name = name
	offset += n

	pass, n, err := GetString(data[offset:])
	if err != nil {
		return r, err
	}
	r.Password = pass
	offset += n

	sid, n, err := GetUint32(data[offset:])
	if err != nil {
		return r, err
	}
	r.SenderAccountID = sid
	offset += n

	rid, n, err := GetUint32(data[offset:])
	if err != nil {
		return r, err
	}
	r.ReceiverAccountID = rid
	offset += n

	amt, _, err := GetFloat64(data[offset:])
	if err != nil {
		return r, err
	}
	r.Amount = amt

	return r, nil
}

// --- Helpers ---

func PutString(buf []byte, s string) []byte {
	strBytes := []byte(s)
	lenBytes := make([]byte, 4)
	binary.BigEndian.PutUint32(lenBytes, uint32(len(strBytes)))
	return append(buf, append(lenBytes, strBytes...)...)
}

func GetString(buf []byte) (string, int, error) {
	if len(buf) < 4 {
		return "", 0, errors.New("buffer too short for string length")
	}
	strLen := int(binary.BigEndian.Uint32(buf[:4]))
	if len(buf) < 4+strLen {
		return "", 0, errors.New("buffer too short for string data")
	}
	return string(buf[4 : 4+strLen]), 4 + strLen, nil
}

func PutFloat64(buf []byte, f float64) []byte {
	bits := math.Float64bits(f)
	b := make([]byte, 8)
	binary.BigEndian.PutUint64(b, bits)
	return append(buf, b...)
}

func GetFloat64(buf []byte) (float64, int, error) {
	if len(buf) < 8 {
		return 0, 0, errors.New("buffer too short for float64")
	}
	bits := binary.BigEndian.Uint64(buf[:8])
	return math.Float64frombits(bits), 8, nil
}

func PutUint32(buf []byte, u uint32) []byte {
	b := make([]byte, 4)
	binary.BigEndian.PutUint32(b, u)
	return append(buf, b...)
}

func GetUint32(buf []byte) (uint32, int, error) {
	if len(buf) < 4 {
		return 0, 0, errors.New("buffer too short for uint32")
	}
	return binary.BigEndian.Uint32(buf[:4]), 4, nil
}
