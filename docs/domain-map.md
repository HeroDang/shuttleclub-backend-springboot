
### Entities (high-level)

-   User
    
-   Group
    
-   GroupMember (role, joinedAt)
    
-   Session
    
-   SessionRSVP (intent)
    
-   SessionAttendance (truth)
    
-   Expense (paidBy)
    
-   Settlement (sessionId, version, createdAt, status)
    
-   SettlementParticipant (settlementId, userId, shareAmount, netAmount…)
    
-   LedgerEntry (settlementId, type: DEBT/PAYMENT/ADJUST, fromUser, toUser, amount)
    
-   Payment (groupId, payerId, payeeId, amount, idempotencyKey, createdAt)
    

### Relations

-   Group 1—N Session
    
-   Group N—N User via GroupMember
    
-   Session 1—N RSVP, 1—N Attendance, 1—N Expense
    
-   Session 1—N Settlement (versioned)
    
-   Settlement 1—N SettlementParticipant
    
-   Settlement 1—N LedgerEntry
    
-   Group 1—N Payment (optionally link to Settlement)
    

### Invariants

-   Settlement participants MUST derive from Attendance(ATTENDED).
    
-   Settlement is immutable; changes require new version.
    
-   Ledger entries are append-only.