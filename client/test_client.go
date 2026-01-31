//only for testing!!!

package main

import (
	"fmt"
	"net"
	"time"

	"sc6103-project/proto"
)

func main() {
	serverAddr := "127.0.0.1:8080"
	udpAddr, err := net.ResolveUDPAddr("udp", serverAddr)
	if err != nil {
		panic(err)
	}

	// Client A Connection (User)
	conn, err := net.DialUDP("udp", nil, udpAddr)
	if err != nil {
		panic(err)
	}
	defer conn.Close()

	fmt.Printf("Client A sending to %s\n", serverAddr)

	// Test 1: Open Account 1 (Alice)
	fmt.Println("\n--- Test 1: Open Account (Alice) ---")
	openReq1 := proto.OpenAccountRequest{
		Name:     "Alice",
		Password: "password123",
		Currency: proto.CurrencyUSD,
		Balance:  1000.00,
	}
	respBody1 := sendAndReceive(conn, proto.MsgRequest, 101, proto.OpOpen, openReq1.Marshal())
	if respBody1 == nil {
		fmt.Println("Test 1 failed")
		return
	}
	aliceID, _, _ := proto.GetUint32(respBody1)
	fmt.Printf("Alice ID: %d\n", aliceID)

	// Test 2: Open Account 2 (Bob)
	fmt.Println("\n--- Test 2: Open Account (Bob) ---")
	openReq2 := proto.OpenAccountRequest{
		Name:     "Bob",
		Password: "passwordBob",
		Currency: proto.CurrencyUSD,
		Balance:  500.00,
	}
	respBody2 := sendAndReceive(conn, proto.MsgRequest, 102, proto.OpOpen, openReq2.Marshal())
	if respBody2 == nil {
		fmt.Println("Test 2 failed")
		return
	}
	bobID, _, _ := proto.GetUint32(respBody2)
	fmt.Printf("Bob ID: %d\n", bobID)

	// Test 3: Transfer Alice -> Bob
	fmt.Println("\n--- Test 3: Transfer 200.00 from Alice to Bob ---")
	transferReq := proto.TransferRequest{
		Name:              "Alice",
		Password:          "password123",
		SenderAccountID:   aliceID,
		ReceiverAccountID: bobID,
		Amount:            200.00,
	}
	sendAndReceive(conn, proto.MsgRequest, 103, proto.OpTransfer, transferReq.Marshal())

	// Test 4: Check Bob Balance
	fmt.Println("\n--- Test 4: Check Bob Balance ---")
	checkReq := proto.AuthRequest{
		Name:      "Bob",
		Password:  "passwordBob",
		AccountID: bobID,
	}
	sendAndReceive(conn, proto.MsgRequest, 104, proto.OpCheckBalance, checkReq.Marshal())

	// --- Test 5: Monitor Flow Integration ---
	fmt.Println("\n--- Test 5: Monitor Flow (Client B) ---")

	// Create separate connection for Client B (Monitor)
	connB, err := net.DialUDP("udp", nil, udpAddr)
	if err != nil {
		panic(err)
	}
	defer connB.Close()

	// 1. Client B subscribes
	fmt.Println("[Client B] Subscribing for 2 seconds...")
	monReq := proto.MonitorRequest{DurationSec: 2}
	connB.Write(proto.MarshalPacket(proto.Packet{
		MessageType: proto.MsgRequest,
		RequestID:   200,
		Operation:   proto.OpMonitor,
		Body:        monReq.Marshal(),
	}))
	// Read Monitor Ack
	readOne(connB, "Client B (Monitor Ack)")

	// 2. Client A triggers Update (Deposit)
	fmt.Println("[Client A] Depositing 50.00...")
	depReq := proto.TransactionRequest{
		Name:      "Alice",
		Password:  "password123",
		AccountID: aliceID,
		Amount:    50.00,
	}
	sendAndReceive(conn, proto.MsgRequest, 105, proto.OpDeposit, depReq.Marshal())

	// 3. Client B should receive Callback
	fmt.Println("[Client B] Listening for callback...")
	// We expect a callback here.
	readOne(connB, "Client B (Callback)")

	// 4. Wait for Expiry
	fmt.Println("Waiting 3 seconds for expiry...")
	time.Sleep(3 * time.Second)

	// 5. Client A triggers Update again (Withdraw)
	fmt.Println("[Client A] Withdrawing 10.00...")
	wdReq := proto.TransactionRequest{
		Name:      "Alice",
		Password:  "password123",
		AccountID: aliceID,
		Amount:    10.00,
	}
	sendAndReceive(conn, proto.MsgRequest, 106, proto.OpWithdraw, wdReq.Marshal())

	// 6. Client B should NOT receive anything
	fmt.Println("[Client B] Checking for silence (Expect Timeout)...")
	connB.SetReadDeadline(time.Now().Add(1 * time.Second))
	buf := make([]byte, 1024)
	n, _, err := connB.ReadFromUDP(buf)
	if err != nil {
		fmt.Println("Success: Client B timed out (No callback received)")
	} else {
		// Verify what we received. Ideally we shouldn't receive anything.
		// NOTE: If we used `sendAndReceive` for Monitor Request, we might have leftover packets? No, we read the ack specifically.
		pkt, _ := proto.UnmarshalPacket(buf[:n])
		fmt.Printf("Unexpected packet on Client B! Op:%d MsgType:%d\n", pkt.Operation, pkt.MessageType)
	}

	fmt.Println("Monitor Flow Test Complete")
}

func sendAndReceive(conn *net.UDPConn, msgType uint8, reqID uint32, op uint8, body []byte) []byte {
	pkt := proto.Packet{
		MessageType: msgType,
		RequestID:   reqID,
		Operation:   op,
		Body:        body,
	}

	data := proto.MarshalPacket(pkt)

	// Retry loop for at-least-once simulation
	for i := 0; i < 3; i++ {
		_, err := conn.Write(data)
		if err != nil {
			fmt.Println("Write error:", err)
			return nil
		}

		conn.SetReadDeadline(time.Now().Add(2 * time.Second))
		buf := make([]byte, 1024)
		n, _, err := conn.ReadFromUDP(buf)
		if err != nil {
			fmt.Println("Timeout or Read error, retrying...")
			continue
		}

		fmt.Printf("Received %d bytes\n", n)

		// Parse response to check for errors
		respPkt, err := proto.UnmarshalPacket(buf[:n])
		if err != nil {
			fmt.Println("Malformed response")
			return nil
		} else {
			fmt.Printf("Response Op: %d, ReqID: %d\n", respPkt.Operation, respPkt.RequestID)
			if respPkt.MessageType == proto.MsgError {
				msg, _, _ := proto.GetString(respPkt.Body)
				fmt.Printf("Error from server: %s\n", msg)
				return nil
			} else {
				// Parse based on expected Op if needed, or just Dump
				if respPkt.Operation == proto.OpOpen {
					id, _, _ := proto.GetUint32(respPkt.Body)
					fmt.Printf("Account Opened! ID: %d\n", id)
				} else if respPkt.Operation == proto.OpCheckBalance || respPkt.Operation == proto.OpDeposit || respPkt.Operation == proto.OpTransfer || respPkt.Operation == proto.OpWithdraw {
					bal, _, _ := proto.GetFloat64(respPkt.Body)
					fmt.Printf("Current Balance: %.2f\n", bal)
				} else {
					msg, _, err := proto.GetString(respPkt.Body)
					if err == nil {
						fmt.Printf("Message: %s\n", msg)
					} else {
						fmt.Printf("Raw Body: %v\n", respPkt.Body)
					}
				}
				return respPkt.Body
			}
		}
	}
	fmt.Println("Failed after retries")
	return nil
}

func readOne(conn *net.UDPConn, label string) {
	conn.SetReadDeadline(time.Now().Add(3 * time.Second))
	buf := make([]byte, 1024)
	n, _, err := conn.ReadFromUDP(buf)
	if err != nil {
		fmt.Printf("[%s] Timeout!\n", label)
		return
	}
	pkt, _ := proto.UnmarshalPacket(buf[:n])
	typeStr := "Unknown"
	if pkt.MessageType == proto.MsgReply {
		typeStr = "Reply"
	}
	if pkt.MessageType == proto.MsgCallback {
		typeStr = "Callback"
	}

	fmt.Printf("[%s] Received %s (Op: %d)\n", label, typeStr, pkt.Operation)
}
