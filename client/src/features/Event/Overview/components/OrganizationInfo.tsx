import { useEffect, useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useQueries, useQuery } from '@tanstack/react-query';

import { isAuthenticated } from '@/api/auth';
import { useCreateInviteCode } from '@/api/mutations/useCreateInviteCode';
import { organizationQueryOptions } from '@/api/queries/organization';
import { OrganizationAPIResponse } from '@/api/types/organizations';
import DefaultImage from '@/assets/icon/ahmadda.webp';
import { Flex } from '@/shared/components/Flex';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { ActionButtons } from './ActionButtons';

type OrganizationProps = OrganizationAPIResponse;

export const OrganizationInfo = ({
  organizationId,
  name,
  description,
  imageUrl,
}: OrganizationProps) => {
  const src = imageUrl || DefaultImage;
  const alt = imageUrl ? `${name} 썸네일` : '기본 이벤트 스페이스 이미지';
  const [inviteCode, setInviteCode] = useState<string>('');
  const { mutateAsync: createInviteCodeMutation } = useCreateInviteCode(Number(organizationId));
  const { data: joinedStatus } = useQuery({
    ...organizationQueryOptions.joinedStatus(Number(organizationId)),
    enabled: !!organizationId && isAuthenticated(),
  });

  const { data: organizationMember } = useQuery({
    ...organizationQueryOptions.profile(Number(organizationId)),
    enabled: !!organizationId && isAuthenticated() && !!joinedStatus?.isMember,
  });

  useEffect(() => {
    if (organizationMember?.isAdmin) {
      createInviteCodeMutation().then((data) => {
        setInviteCode(data.inviteCode);
      });
    }
  }, [organizationId, createInviteCodeMutation, organizationMember?.isAdmin]);

  return (
    <Flex
      as="section"
      alignItems="flex-end"
      justifyContent="space-between"
      width="100%"
      gap="20px"
      margin="0px auto"
      padding="80px 0 10px 0"
      css={css`
        @media (max-width: 768px) {
          padding: 80px 0 0 0;
        }
      `}
    >
      <Flex dir="column">
        <Flex padding="0 10px" justifyContent="space-between" alignItems="center" width="100%">
          <Flex
            justifyContent="space-between"
            alignItems="center"
            padding="10px"
            gap="20px"
            css={css`
              @media (max-width: 481px) {
                flex-direction: column;
                align-items: flex-start;
                gap: 10px;
              }
            `}
            aria-labelledby="org-info-name"
            aria-describedby="org-info-description"
            aria-label={`${name} 스페이스 입니다. ${name}에 대한 설명은 다음과 같습니다. ${description}`}
          >
            <ThumbImg
              src={src}
              alt={alt}
              width={255}
              height={255}
              decoding="async"
              fetchPriority="high"
              onError={(e) => {
                e.currentTarget.onerror = null;
                e.currentTarget.src = DefaultImage;
              }}
            />

            <Flex dir="column" gap="8px">
              <Text type="Display" weight="bold" id="org-info-name" aria-hidden="true">
                {name}
              </Text>
              <Spacing height="4px" />
              <Text as="h2" type="Heading" id="org-info-description" aria-hidden="true">
                {description}
              </Text>
              {organizationMember?.isAdmin && (
                <Text type="Body" color={theme.colors.gray500}>
                  {`초대 코드: ${inviteCode}`}
                </Text>
              )}
            </Flex>
          </Flex>
        </Flex>
      </Flex>
      <ActionButtons
        isAdmin={organizationMember?.isAdmin ?? false}
        isMember={(isAuthenticated() && joinedStatus?.isMember) ?? false}
      />
    </Flex>
  );
};

const ThumbImg = styled.img`
  border-radius: 12px;
  border: 1px solid ${theme.colors.gray100};
  width: clamp(140px, 30vw, 175px);
  height: clamp(140px, 30vw, 175px);
  display: block;
  object-fit: scale-down;

  @media (max-width: 481px) {
    margin-right: 0;
    margin-bottom: 8px;
  }
`;
