package main

import (
	"flag"
	"fmt"
	"math/rand"
	"net"
	"os"
	"sc6103-project/proto"
)

func main() {
	portPtr := flag.Int("port", 8080, "UDP port to listen on")
	lossPtr := flag.Int("loss", 0, "Packet loss probability (0-100)")
	flag.Parse()

	addr := net.UDPAddr{
		Port: *portPtr,
		IP:   net.ParseIP("0.0.0.0"),
	}

	conn, err := net.ListenUDP("udp", &addr)
	if err != nil {
		fmt.Printf("Error listening on port %d: %v\n", *portPtr, err)
		os.Exit(1)
	}
	defer conn.Close()

	fmt.Printf("Server listening on 0.0.0.0:%d\n", *portPtr)
	fmt.Printf("Packet Loss Rate: %d%%\n", *lossPtr)

	// Seed random for loss simulation
	// rand.Seed(time.Now().UnixNano())

	svc := NewService(conn, *lossPtr)
	semantics := NewSemanticsManager()

	buffer := make([]byte, 4096)

	for {
		n, remoteAddr, err := conn.ReadFromUDP(buffer)
		if err != nil {
			fmt.Printf("Error reading content: %v\n", err)
			continue
		}

		// Inbound Packet Loss Simulation
		if rand.Intn(100) < *lossPtr {
			fmt.Printf("Simulated DROP request from %v\n", remoteAddr)
			continue
		}

		// Copy data to avoid buffer overwrites in next iteration
		data := make([]byte, n)
		copy(data, buffer[:n])

		// Unmarshal Packet
		pkt, err := proto.UnmarshalPacket(data)
		if err != nil {
			fmt.Printf("Malformed packet from %v: %v\n", remoteAddr, err)
			continue
		}

		fmt.Printf("Received %d bytes from %v. Op: %d, ReqID: %d\n", n, remoteAddr, pkt.Operation, pkt.RequestID)

		clientKey := remoteAddr.String()

		// Check Invocation Semantics
		isDup, cachedReply := semantics.CheckDuplicate(clientKey, pkt.RequestID, pkt.Operation)
		if isDup {
			fmt.Printf("Duplicate request %d from %v. Resending cached reply.\n", pkt.RequestID, remoteAddr)
			// Resend cached reply
			sendReply(conn, remoteAddr, cachedReply, *lossPtr)
			continue
		}

		// Dispatch to Service
		var replyBody []byte
		var procErr error

		switch pkt.Operation {
		case proto.OpOpen:
			replyBody, procErr = svc.OpenAccount(pkt.Body)
		case proto.OpClose:
			replyBody, procErr = svc.CloseAccount(pkt.Body)
		case proto.OpDeposit:
			replyBody, procErr = svc.Deposit(pkt.Body)
		case proto.OpWithdraw:
			replyBody, procErr = svc.Withdraw(pkt.Body)
		case proto.OpMonitor:
			replyBody, procErr = svc.RegisterMonitor(pkt.Body, remoteAddr)
		case proto.OpCheckBalance:
			replyBody, procErr = svc.CheckBalance(pkt.Body)
		case proto.OpApplyInterest:
			replyBody, procErr = svc.ApplyInterest(pkt.Body)
		default:
			procErr = fmt.Errorf("unknown operation %d", pkt.Operation)
		}

		// Construct Reply Packet
		var replyPkt proto.Packet
		replyPkt.RequestID = pkt.RequestID

		if procErr != nil {
			replyPkt.MessageType = proto.MsgError
			replyPkt.Operation = pkt.Operation
			replyPkt.Body = proto.PutString(nil, procErr.Error())
			fmt.Printf("Operation Error: %v\n", procErr)
		} else {
			replyPkt.MessageType = proto.MsgReply
			replyPkt.Operation = pkt.Operation
			replyPkt.Body = replyBody
		}

		replyBytes := proto.MarshalPacket(replyPkt)

		// Update History (Semantics)
		semantics.UpdateHistory(clientKey, pkt.RequestID, pkt.Operation, replyBytes)

		// Send Reply
		sendReply(conn, remoteAddr, replyBytes, *lossPtr)
	}
}

func sendReply(conn *net.UDPConn, addr *net.UDPAddr, data []byte, lossRate int) {
	// Outbound Loss Simulation
	if rand.Intn(100) < lossRate {
		fmt.Printf("Simulated DROP reply to %v\n", addr)
		return
	}

	_, err := conn.WriteToUDP(data, addr)
	if err != nil {
		fmt.Printf("Error sending reply to %v: %v\n", addr, err)
	}
}
