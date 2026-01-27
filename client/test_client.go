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

	conn, err := net.DialUDP("udp", nil, udpAddr)
	if err != nil {
		panic(err)
	}
	defer conn.Close()

	fmt.Printf("Client sending to %s\n", serverAddr)

	// Test 1: Open Account
	fmt.Println("\n--- Test 1: Open Account ---")
	openReq := proto.OpenAccountRequest{
		Name:     "Alice",
		Password: "password123",
		Currency: proto.CurrencyUSD,
		Balance:  1000.50,
	}

	respBody1 := sendAndReceive(conn, proto.MsgRequest, 101, proto.OpOpen, openReq.Marshal())
	if respBody1 == nil {
		fmt.Println("Test 1 failed")
		return
	}

	accID, _, _ := proto.GetUint32(respBody1)
	fmt.Printf("Parsed Account ID: %d\n", accID)

	// Test 2: Check Balance (Idempotent)
	fmt.Println("\n--- Test 2: Check Balance (Idempotency Check) ---")

	checkReq := proto.AuthRequest{
		Name:      "Alice",
		Password:  "password123",
		AccountID: accID,
	}

	sendAndReceive(conn, proto.MsgRequest, 102, proto.OpCheckBalance, checkReq.Marshal())
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
				} else if respPkt.Operation == proto.OpCheckBalance || respPkt.Operation == proto.OpDeposit {
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
