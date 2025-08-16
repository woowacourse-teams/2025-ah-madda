import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';

import { OrgCard } from '../components/OrgCard';

type Org = {
  organizationId: number;
  name: string;
  thumbnailUrl: string;
  description: string;
};

const organizations: Org[] = [
  { organizationId: 1, name: '조직1', thumbnailUrl: '/icon-512x512.png', description: '설명' },
  { organizationId: 2, name: '조직2', thumbnailUrl: '/icon-512x512.png', description: '설명' },
  { organizationId: 3, name: '조직3', thumbnailUrl: '/icon-512x512.png', description: '설명' },
  { organizationId: 4, name: '조직4', thumbnailUrl: '/icon-512x512.png', description: '설명' },
];

export const OrganizationSelectPage = () => {
  const navigate = useNavigate();
  const handleCreateClick = () => navigate('/organizations/new');
  const handleJoin = () => navigate('/event'); // A.TODO: 확정 시 교체

  return (
    <PageLayout
      header={
        <Header
          left={
            <Icon
              name="logo"
              size={55}
              onClick={() => navigate('/event')}
              css={css`
                cursor: pointer;
              `}
            />
          }
          right={
            <Button color="secondary" onClick={handleCreateClick}>
              조직 생성하기
            </Button>
          }
        />
      }
    >
      <Flex
        dir="column"
        width="100%"
        css={css`
          flex: 1;
          margin-top: 60px;
        `}
        justifyContent="center"
        alignItems="center"
        gap="32px"
        padding="28px 20px"
      >
        <Flex
          dir="column"
          alignItems="center"
          gap="4px"
          css={css`
            width: 100%;
            @media (max-width: 720px) {
              width: 312px;
              margin-left: auto;
              margin-right: auto;
            }
          `}
        >
          <Text as="h1" type="Display" weight="bold">
            조직에 참여하고,
          </Text>
          <Text as="h1" type="Display" weight="bold">
            이벤트를 놓치지 마세요.
          </Text>
        </Flex>

        <Flex
          justifyContent="center"
          gap="80px"
          css={css`
            flex-wrap: wrap;
          `}
        >
          {organizations.map((org) => (
            <OrgCard key={org.organizationId} org={org} onJoin={handleJoin} />
          ))}
        </Flex>
      </Flex>
    </PageLayout>
  );
};
