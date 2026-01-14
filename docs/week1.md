
## Week 1 goals achieved

  

- ✅ Spring Boot skeleton (Java 17) chạy  ổn  trong STS

- ✅ PostgreSQL local bằng Docker Compose + healthcheck

- ✅ API conventions:

- ApiResponse unified

- GlobalExceptionHandler unified

- Validation errors normalized

- RequestId filter (X-Request-Id + MDC)

- ✅ Bounded-context package skeleton cho: group/session/expense/settlement/payment

- ✅ PRD mini + Domain map hoàn  chỉnh

  

### How to run locally

  

`docker compose up -d `

`# run Spring Boot with profile local`

  

### Smoke tests

  

- `GET /health`

- `GET /health/db`

- `POST /groups` (validation)

- `GET /groups/{id}` (404 demo)

  

## Week 2 – Core Identity & Membership

- Implemented user, club, and membership core domain

- Membership modeled as first-class entity with lifecycle

- Enforced business invariants at DB + service layer

- APIs validated via end-to-end manual testing

- Naming decision: “club = group”