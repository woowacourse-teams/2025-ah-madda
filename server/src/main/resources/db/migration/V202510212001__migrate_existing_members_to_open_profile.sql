INSERT INTO open_profile (member_id, organization_group_id, nickname, created_at, updated_at)
SELECT m.member_id,
       5,
       m.name,
       NOW(),
       NOW()
FROM member m;
