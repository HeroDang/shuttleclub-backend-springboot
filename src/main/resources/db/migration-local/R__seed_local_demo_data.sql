-- V2__seed_local_demo_data.sql
-- Local demo seed data: users, club, memberships
-- Safe to re-run using ON CONFLICT

-- 1) Users
INSERT INTO users (display_name, email, status)
VALUES
  ('Hung (Owner)', 'local_hung@test.com', 'ACTIVE'),
  ('Admin A',      'local_admin@test.com', 'ACTIVE'),
  ('Member B',     'local_member@test.com', 'ACTIVE'),
  ('Invite C',     'local_invite@test.com', 'ACTIVE')
ON CONFLICT DO NOTHING;

-- 2) Club (created by Hung)
INSERT INTO clubs (name, created_by_user_id)
SELECT
  'CLB Demo',
  u.id
FROM users u
WHERE u.email = 'local_hung@test.com'
ON CONFLICT DO NOTHING;

-- 3) Owner membership (ACTIVE OWNER)
INSERT INTO club_members (club_id, user_id, role, status, joined_at)
SELECT
  c.id,
  u.id,
  'OWNER',
  'ACTIVE',
  now()
FROM clubs c
JOIN users u ON u.email = 'local_hung@test.com'
WHERE c.name = 'CLB Demo'
ON CONFLICT DO NOTHING;

-- 4) Admin membership (ACTIVE ADMIN)
INSERT INTO club_members (club_id, user_id, role, status, joined_at, invited_by_user_id)
SELECT
  c.id,
  admin_u.id,
  'ADMIN',
  'ACTIVE',
  now(),
  owner_u.id
FROM clubs c
JOIN users owner_u ON owner_u.email = 'local_hung@test.com'
JOIN users admin_u ON admin_u.email = 'local_admin@test.com'
WHERE c.name = 'CLB Demo'
ON CONFLICT DO NOTHING;

-- 5) Member membership (ACTIVE MEMBER)
INSERT INTO club_members (club_id, user_id, role, status, joined_at, invited_by_user_id)
SELECT
  c.id,
  mem_u.id,
  'MEMBER',
  'ACTIVE',
  now(),
  owner_u.id
FROM clubs c
JOIN users owner_u ON owner_u.email = 'local_hung@test.com'
JOIN users mem_u ON mem_u.email = 'local_member@test.com'
WHERE c.name = 'CLB Demo'
ON CONFLICT DO NOTHING;

-- 6) Invited membership (INVITED MEMBER) for Invite C
INSERT INTO club_members (club_id, user_id, role, status, invited_by_user_id)
SELECT
  c.id,
  inv_u.id,
  'MEMBER',
  'INVITED',
  owner_u.id
FROM clubs c
JOIN users owner_u ON owner_u.email = 'local_hung@test.com'
JOIN users inv_u ON inv_u.email = 'local_invite@test.com'
WHERE c.name = 'CLB Demo'
ON CONFLICT DO NOTHING;
