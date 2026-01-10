-- V1__create_core_identity.sql
-- Core identity & membership schema
-- Users / Clubs / Club Members

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================
-- users
-- =========================
CREATE TABLE users (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  display_name  varchar(100) NOT NULL,
  email         varchar(255),
  phone         varchar(30),

  status        varchar(20) NOT NULL DEFAULT 'ACTIVE'
    CHECK (status IN ('ACTIVE', 'DISABLED')),

  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_users_email
  ON users (email)
  WHERE email IS NOT NULL;

CREATE UNIQUE INDEX uq_users_phone
  ON users (phone)
  WHERE phone IS NOT NULL;

-- =========================
-- clubs
-- =========================
CREATE TABLE clubs (
  id                  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name                varchar(120) NOT NULL,
  created_by_user_id  uuid NOT NULL REFERENCES users(id),

  created_at          timestamptz NOT NULL DEFAULT now(),
  updated_at          timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_clubs_creator_name
  ON clubs (created_by_user_id, name);

-- =========================
-- club_members
-- =========================
CREATE TABLE club_members (
  id                  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  club_id             uuid NOT NULL REFERENCES clubs(id) ON DELETE CASCADE,
  user_id             uuid NOT NULL REFERENCES users(id),

  role                varchar(20) NOT NULL
    CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER')),

  status              varchar(20) NOT NULL
    CHECK (status IN ('ACTIVE', 'INVITED', 'LEFT')),

  invited_by_user_id  uuid REFERENCES users(id),
  joined_at           timestamptz,
  left_at             timestamptz,

  created_at          timestamptz NOT NULL DEFAULT now(),
  updated_at          timestamptz NOT NULL DEFAULT now(),

  CONSTRAINT uq_club_members_club_user
    UNIQUE (club_id, user_id)
);

CREATE INDEX idx_club_members_club
  ON club_members (club_id);

CREATE INDEX idx_club_members_user
  ON club_members (user_id);

-- Ensure only 1 ACTIVE OWNER per club
CREATE UNIQUE INDEX uq_club_active_owner
  ON club_members (club_id)
  WHERE role = 'OWNER' AND status = 'ACTIVE';
