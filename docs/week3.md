
# Week 3 — Session & RSVP

## Mục tiêu
Xây dựng chức năng **Session (buổi đánh)** và **RSVP (ý định tham gia)**, làm nền cho Attendance (tuần 4) và Settlement (tuần sau).

---

## Domain decisions
- **Session là aggregate root**
- **RSVP chỉ là intent**, không dùng cho chia tiền
- Attendance (tuần 4) sẽ là **source of truth**

### Session lifecycle
- `SCHEDULED`: mở RSVP
- `CLOSED`: khóa RSVP, chuẩn bị Attendance  
→ Thêm lifecycle sớm để tránh đập lại schema/API.

---

## Database design

### `sessions`
- `id`, `club_id`, `title`, `start_time`, `location`, `note`
- `status` (SCHEDULED / CLOSED)
- `created_by`
- audit: `created_at`, `updated_at`
- index: `(club_id, start_time DESC, id DESC)`

### `session_rsvps`
- `id`, `session_id`, `user_id`
- `status` (YES / NO / MAYBE)
- `responded_at`
- unique `(session_id, user_id)`
- FK `session_id` ON DELETE CASCADE

---

## API design

### Create session
POST /clubs/{clubId}/sessions
- User phải là member ACTIVE
- `start_time >= now - 6h`

### List sessions
GET /clubs/{clubId}/sessions
Query:
- `status` (optional)
- `from`, `to`
- `page`, `size`

Response:

    {
      "items": [...],
      "pageInfo": { "page", "size", "totalItems", "totalPages" }
    }
→ Không expose Spring `Page`.
### Update RSVP
PUT /sessions/{sessionId}/rsvps/me
-   Chỉ member ACTIVE
-   Bị khóa nếu session `CLOSED`
### Close session
POST /sessions/{sessionId}/close
-   Chỉ OWNER / ADMIN
-   Idempotent
-   Chuẩn bị cho Attendance

## Authorization

-   Dùng `CurrentUserProvider` (mockable)
-   Local/dev: `X-User-Id` header
-   Membership & role check bằng JPQL `@Query` (ManyToOne safe)

## Chuẩn bị cho Week 4 — Attendance

-   Session có trạng thái `CLOSED`
-   RSVP bị khóa trước khi record attendance
-   Attendance sẽ là bảng riêng, không reuse RSVP