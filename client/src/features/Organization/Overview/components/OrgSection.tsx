import { useEffect, useRef } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';
import { OrganizationAPIResponse } from '@/api/types/organizations';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { OrgCard } from './OrgCard';

export type OrgSectionProps = {
  organizations: OrganizationAPIResponse[];
};
export const OrgSection = ({ organizations }: OrgSectionProps) => {
  const navigate = useNavigate();
  const total = organizations.length;
  const announceRef = useRef<HTMLSpanElement>(null);

  useEffect(() => {
    announceRef.current?.focus();
  }, []);

  const handleJoin = (orgId: number) => navigate(`/${orgId}/event`);

  return (
    <Flex dir="column" gap="24px" margin="60px 0 0 0" padding="40px 20px 0 20px">
      <VisuallyHidden as="h1" ref={announceRef} tabIndex={-1}>
        스페이스 목록 안내. 현재 스페이스는 총 {total}개입니다. 현재 가장 활발한 순서대로 스페이스를
        노출합니다.
      </VisuallyHidden>
      <Flex
        dir="row"
        justifyContent="space-between"
        alignItems="flex-start"
        gap="8px"
        width="100%"
        margin="0 0 20px 0"
        css={css`
          @media (max-width: 768px) {
            flex-direction: column;
            align-items: flex-end;
            gap: 10px;
          }
        `}
      >
        <Flex dir="column" gap="8px" alignItems="flex-start" width="100%">
          <Text as="h1" type="Display" weight="bold" aria-hidden="true">
            스페이스 목록 ({total})
          </Text>
          <Text as="h3" type="Body" color={theme.colors.gray500} aria-hidden="true">
            현재 가장 활발한 순서대로 스페이스를 노출해요.
          </Text>
        </Flex>
        {isAuthenticated() && (
          <Flex dir="column" gap="8px" alignItems="flex-end" width="100%">
            <Button
              size="md"
              color="primary"
              variant="solid"
              iconName="plus"
              aria-label="스페이스 만들기 버튼입니다. 클릭하여 스페이스 만들기 페이지로 이동하세요."
              onClick={() => navigate(`/organization/new`)}
            >
              스페이스 만들기
            </Button>
          </Flex>
        )}
      </Flex>
      <OrgListContainer
        dir="column"
        width="100%"
        gap="8px"
        margin="0 0 40px 0"
        role="list"
        aria-label="스페이스 목록"
      >
        <DeskTopOrgList>
          {organizations.map((org, idx) => (
            <OrgCard
              key={org.organizationId}
              organization={org}
              position={idx + 1}
              total={total}
              onJoin={() => handleJoin(org.organizationId)}
            />
          ))}
        </DeskTopOrgList>

        <MobileOrgList dir="column">
          {organizations.map((org, idx) => (
            <OrgCard
              key={org.organizationId}
              organization={org}
              position={idx + 1}
              total={total}
              onJoin={() => handleJoin(org.organizationId)}
            />
          ))}
        </MobileOrgList>
      </OrgListContainer>

      {total === 0 && (
        <Text type="Body" color={theme.colors.gray500}>
          아직 스페이스가 없어요. 새로운 스페이스를 만들어보세요!
        </Text>
      )}
    </Flex>
  );
};

const OrgListContainer = styled(Flex)`
  max-height: 50vh;
  overflow-y: auto;
`;

const DeskTopOrgList = styled.div`
  @media (min-width: 768px) {
    width: 100%;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 3fr));
    flex-wrap: nowrap;
    overflow-x: auto;
    gap: 20px;
  }

  @media (max-width: 768px) {
    display: none;
  }
`;

const MobileOrgList = styled(Flex)`
  display: none;

  @media (max-width: 768px) {
    display: flex;
    gap: 8px;
    width: 100%;
    flex-shrink: 0;
    overflow-y: auto;
  }
`;

const VisuallyHidden = styled.span`
  position: absolute !important;
  width: 1px;
  height: 1px;
  margin: -1px;
  padding: 0;
  border: 0;
  overflow: hidden;
  clip: rect(0 0 0 0);
  clip-path: inset(50%);
  white-space: nowrap;
`;
