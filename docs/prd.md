
### 1) Product summary

**ShuttleClub** là backend phục vụ quản lý CLB/nhóm cầu lông tại TP.HCM: tạo buổi đánh, chốt attendance thực tế, ghi chi phí và chia tiền minh bạch. Backend ưu tiên correctness và auditability.

### 2) Personas

**Organizer (Owner/Manager):**

-   Tạo session, chốt attendance, ghi expense, generate settlement, theo dõi nợ và nhắc thanh toán.
    

**Member:**

-   RSVP, xem attendance, xem mình nợ ai bao nhiêu, ghi nhận payment.
    

### 3) Core problems

-   RSVP không phản ánh thực tế → chia tiền sai, mâu thuẫn.
    
-   Chia tiền thủ công → thiếu minh bạch, không truy vết được.
    
-   Thanh toán lắt nhắt → khó nhớ ai đã trả.
    

### 4) MVP scope (backend-first)

**In-scope:**

-   Group + members (roles: OWNER/MEMBER)
    
-   Session: create + RSVP + finalize attendance
    
-   Expense: add expense (paidBy)
    
-   Settlement: generate snapshot + version theo attendance
    
-   Ledger: ghi sổ audit các phát sinh nợ và payment
    
-   Payment: ghi nhận thanh toán idempotent
    

**Out-of-scope:**

-   Booking sân/slot management
    
-   Payment gateway/bank integration
    
-   Realtime/chat/notification push
    

### 5) Key principles (non-negotiable)

1.  **Attendance là source of truth** cho split.
    
2.  **Settlement snapshot + version**, không tính động.
    
3.  **Ledger immutable** để audit.
    
4.  **Idempotency** cho generate settlement và create payment.
    
5.  Ưu tiên clarity/maintainability/correctness hơn code nhanh.
    

### 6) Primary flows

**Flow A — Session lifecycle**

1.  Organizer tạo session.
    
2.  Members RSVP (GOING/MAYBE/NOT_GOING).
    
3.  Sau khi đánh xong, Organizer chốt attendance (ATTENDED/ABSENT).  
    → Attendance được “lock” cho settlement version tương ứng.
    

**Flow B — Expense**

-   Add expense vào session: amount, category, paidBy.
    
-   Expense được tính vào settlement version tại thời điểm generate.
    

**Flow C — Generate settlement (snapshot + version)**

-   POST `/sessions/{id}/settlements`
    
-   System tạo Settlement vN:
    
    -   participants = attendance ATTENDED
        
    -   snapshot expense list tại thời điểm generate
        
    -   tính split (MVP: equal split)
        
    -   sinh ledger entries (DEBT) để audit
        
-   Nếu attendance/expense thay đổi: tạo v(N+1), không sửa vN.
    

**Flow D — Payment**

-   Member A trả Member B amount.
    
-   POST `/payments` với `Idempotency-Key`
    
-   System tạo payment record + ledger entries PAYMENT để đối soát.
    

### 7) Non-functional requirements (MVP)

-   Transaction boundary rõ (generate settlement)
    
-   Error & response format thống nhất
    
-   RequestId traceable
    
-   Validation strict (400), business conflict (409), not found (404)