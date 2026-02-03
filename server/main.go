package main

import (
	"encoding/hex"
	"flag"
	"fmt"
	"math/rand"
	"net"
	"os"
	"time"

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
	addrs, _ := net.InterfaceAddrs()
	fmt.Println("Server LAN IPs:")
	for _, address := range addrs {
		if ipnet, ok := address.(*net.IPNet); ok && !ipnet.IP.IsLoopback() {
			if ipnet.IP.To4() != nil {
				fmt.Println("- " + ipnet.IP.String())
			}
		}
	}
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
			fmt.Printf("[DROP IN] Dropped request from %v\n", remoteAddr)
			continue
		}

		// Copy data to avoid buffer overwrites in next iteration
		data := make([]byte, n)
		copy(data, buffer[:n])

		// Unmarshal Packet
		pkt, err := proto.UnmarshalPacket(data)
		if err != nil {
			fmt.Printf("[MALFORMED] From %v: %v\n", remoteAddr, err)
			continue
		}

		// Log Request reception
		logPacket("RECV", remoteAddr, pkt)

		clientKey := remoteAddr.String()

		// Check Invocation Semantics
		isDup, cachedReply := semantics.CheckDuplicate(clientKey, pkt.RequestID, pkt.Operation)
		if isDup {
			fmt.Printf("[DUPLICATE] Request %d from %v. Resending cached reply.\n", pkt.RequestID, remoteAddr)
			// Log Resend
			logRawPacket("SEND (CACHE)", remoteAddr, cachedReply)

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
		case proto.OpTransfer:
			replyBody, procErr = svc.Transfer(pkt.Body)
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
			// fmt.Printf("Operation Error: %v\n", procErr)
		} else {
			replyPkt.MessageType = proto.MsgReply
			replyPkt.Operation = pkt.Operation
			replyPkt.Body = replyBody
		}

		replyBytes := proto.MarshalPacket(replyPkt)

		// Log Reply
		logPacket("SEND", remoteAddr, replyPkt)

		// Update History (Semantics)
		semantics.UpdateHistory(clientKey, pkt.RequestID, pkt.Operation, replyBytes)

		// Send Reply
		sendReply(conn, remoteAddr, replyBytes, *lossPtr)
	}
}

func sendReply(conn *net.UDPConn, addr *net.UDPAddr, data []byte, lossRate int) {
	// Outbound Loss Simulation
	if rand.Intn(100) < lossRate {
		fmt.Printf("[DROP OUT] Dropped reply to %v\n", addr)
		return
	}

	_, err := conn.WriteToUDP(data, addr)
	if err != nil {
		fmt.Printf("Error sending reply to %v: %v\n", addr, err)
	}
}

func logPacket(direction string, addr net.Addr, pkt proto.Packet) {
	timestamp := time.Now().Format("15:04:05.000")
	opName := proto.OpName(pkt.Operation)
	msgType := proto.MsgTypeName(pkt.MessageType)

	// Format: [Time] [DIR] [Addr] | Type: Request | Op: OpName | ReqID: 123 | Len: 20
	// Body Hex Dump limited

	fmt.Printf("[%s] [%-4s] %s | Type: %-8s | Op: %-14s | ID: %d | Len: %d\n",
		timestamp, direction, addr.String(), msgType, opName, pkt.RequestID, len(pkt.Body))

	// Optional: Print a small hex dump of the body for "Wireshark feel"
	if len(pkt.Body) > 0 {
		fmt.Printf("    Body: %s\n", hex.EncodeToString(pkt.Body))
	}
}

func logRawPacket(direction string, addr net.Addr, data []byte) {
	// Try parsing
	pkt, err := proto.UnmarshalPacket(data)
	if err == nil {
		logPacket(direction, addr, pkt)
	} else {
		// Fallback
		timestamp := time.Now().Format("15:04:05.000")
		fmt.Printf("[%s] [%-4s] %s | RAW PACKET | Len: %d\n", timestamp, direction, addr.String(), len(data))
	}
}
