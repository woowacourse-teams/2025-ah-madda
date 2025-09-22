ALTER TABLE organization_group
    DROP FOREIGN KEY FK_group__org__org_id,
    DROP COLUMN organization_id;

INSERT INTO organization_group (organization_group_id, name, created_at, updated_at)
VALUES (1, '코치', NOW(6), NOW(6)),
       (2, '백엔드', NOW(6), NOW(6)),
       (3, '프론트', NOW(6), NOW(6)),
       (4, '안드로이드', NOW(6), NOW(6)),
       (5, '기타', NOW(6), NOW(6));

ALTER TABLE organization_member
    ADD COLUMN organization_group_id BIGINT NULL,
    ADD CONSTRAINT FK_org_member__org_group__group_id
        FOREIGN KEY (organization_group_id) REFERENCES organization_group (organization_group_id);

-- 모든 기존 조직원을 group_id = 5 (기타)로 업데이트
UPDATE organization_member
SET organization_group_id = 5;

ALTER TABLE organization_member
    MODIFY COLUMN organization_group_id BIGINT NOT NULL;
