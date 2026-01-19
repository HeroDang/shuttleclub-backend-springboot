-- V2__create_sessions_and_rsvps.sql
-- Create sessions + session_rsvps for Week 3 (Session + RSVP)

-- =========================
-- sessions
-- =========================
CREATE TABLE IF NOT EXISTS sessions (
    id           UUID PRIMARY KEY,
    club_id      UUID NOT NULL,
    title        VARCHAR(100) NOT NULL,
    start_time   TIMESTAMPTZ NOT NULL,
    location     VARCHAR(255) NOT NULL,
    note         VARCHAR(1000),
    status       VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_by   UUID NOT NULL,

    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_sessions_club
        FOREIGN KEY (club_id) REFERENCES clubs (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_sessions_created_by
        FOREIGN KEY (created_by) REFERENCES users (id)
        ON DELETE RESTRICT,

    CONSTRAINT ck_sessions_status
        CHECK (status IN ('SCHEDULED', 'CLOSED'))
);

-- Index for listing sessions by club + time (stable pagination)
CREATE INDEX IF NOT EXISTS idx_sessions_club_start_time
    ON sessions (club_id, start_time DESC, id DESC);

-- Optional index if later you filter a lot by status (keep commented for now)
-- CREATE INDEX IF NOT EXISTS idx_sessions_club_status_start_time
--     ON sessions (club_id, status, start_time DESC, id DESC);


-- =========================
-- session_rsvps
-- =========================
CREATE TABLE IF NOT EXISTS session_rsvps (
    id            UUID PRIMARY KEY,
    session_id    UUID NOT NULL,
    user_id       UUID NOT NULL,
    status        VARCHAR(10) NOT NULL,
    responded_at  TIMESTAMPTZ,

    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_session_rsvps_session
        FOREIGN KEY (session_id) REFERENCES sessions (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_session_rsvps_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_session_rsvps_session_user
        UNIQUE (session_id, user_id),

    CONSTRAINT ck_session_rsvps_status
        CHECK (status IN ('YES', 'NO', 'MAYBE'))
);

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_session_rsvps_session
    ON session_rsvps (session_id);

CREATE INDEX IF NOT EXISTS idx_session_rsvps_user_responded_at
    ON session_rsvps (user_id, responded_at DESC);
