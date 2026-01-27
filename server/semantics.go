package main

import (
	"sc6103-project/proto"
)

// HistoryEntry stores the last reply for a client to handle retransmissions
type HistoryEntry struct {
	LastRequestID uint32
	ReplyPayload  []byte
}

// SemanticsManager handles the invocation semantics logic
type SemanticsManager struct {
	history map[string]*HistoryEntry // Key: Client IP:Port string
}

func NewSemanticsManager() *SemanticsManager {
	return &SemanticsManager{
		history: make(map[string]*HistoryEntry),
	}
}

// isIdempotent returns true if the operation is safe to re-execute without history
// Idempotent: Can use At-Least-Once (CheckBalance)
// Non-Idempotent: Must use At-Most-Once (Deposit, Withdraw, Open, Close, etc.)
func (sm *SemanticsManager) isIdempotent(op uint8) bool {
	// Only OpCheckBalance is strictly idempotent in this system design context
	// where we want to demonstrate the difference.
	// OpMonitor might be, but let's stick to the core constraint.
	return op == proto.OpCheckBalance
}

// CheckDuplicate returns (isDuplicate, cachedReply)
// Uses opCode to decide strategy.
func (sm *SemanticsManager) CheckDuplicate(clientKey string, reqID uint32, op uint8) (bool, []byte) {
	// Strategy:
	// If Idempotent -> At-Least-Once -> Do not check/use history (Always re-execute)
	// If Non-Idempotent -> At-Most-Once -> Check history

	if sm.isIdempotent(op) {
		// At-Least-Once: process every request
		return false, nil
	}

	// At-Most-Once Logic
	entry, exists := sm.history[clientKey]
	if !exists {
		return false, nil
	}

	if reqID == entry.LastRequestID {
		// Duplicate detected! Return cached reply
		return true, entry.ReplyPayload
	}

	return false, nil
}

// UpdateHistory saves the reply if necessary (At-Most-Once)
func (sm *SemanticsManager) UpdateHistory(clientKey string, reqID uint32, op uint8, reply []byte) {
	if sm.isIdempotent(op) {
		// At-Least-Once: No need to store history
		return
	}

	// At-Most-Once: Store history
	replyCopy := make([]byte, len(reply))
	copy(replyCopy, reply)

	sm.history[clientKey] = &HistoryEntry{
		LastRequestID: reqID,
		ReplyPayload:  replyCopy,
	}
}
