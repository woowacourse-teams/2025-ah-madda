import { useEffect, useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams, useNavigate } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';
import { useParticipateOrganization } from '@/api/mutations/useParticipateOrganization';
import { organizationQueryOptions } from '@/api/queries/organization';
import Woowa from '@/assets/icon/wowaw.png';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';
import { useModal } from '@/shared/hooks/useModal';
import { theme } from '@/shared/styles/theme';

export const InviteModal = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const [nickname, setNickname] = useState('');
  const inviteCode = searchParams.get('code');
  const { close } = useModal();

  const { data: organizationData } = useQuery(organizationQueryOptions.preview(inviteCode ?? ''));
  const { mutate: joinOrganization } = useParticipateOrganization(
    organizationData?.organizationId ?? 0
  );

  const handleCloseModal = () => {
    close();
    navigate('/');
  };

  const handleSubmitNickname = () => {
    joinOrganization(
      {
        nickname,
        inviteCode: inviteCode ?? '',
      },
      {
        onSuccess: () => {
          alert('조직 참가가 완료되었습니다!');
          handleCloseModal();
          navigate('/event');
        },
        onError: (error) => {
          alert(`${error.message}`);
          navigate('/');
        },
      }
    );
  };

  useEffect(() => {
    if (!inviteCode) {
      navigate('/');
    }

    if (!isAuthenticated()) {
      alert('로그인이 필요한 서비스입니다. 먼저 로그인해 주세요.');
      navigate('/');
    }
  }, [inviteCode, navigate]);

  // S.TODO : imgurl 처리
  return (
    <Modal
      isOpen={true}
      onClose={handleCloseModal}
      css={css`
        width: 380px;
      `}
    >
      <Flex justifyContent="space-between" alignItems="baseline">
        <Text type="Heading" weight="bold" color="#333">
          닉네임 설정
        </Text>
      </Flex>

      <Flex dir="column" alignItems="center">
        <Img src={Woowa} alt={organizationData?.name} />
        <Text type="Body" weight="regular" color="#666">
          <Text as="span" type="Body" weight="bold" color={theme.colors.primary700}>
            {organizationData?.name}
          </Text>
          에서 사용할 닉네임을 입력해주세요.
        </Text>

        <Input
          id="nickname"
          label=""
          type="text"
          placeholder="닉네임을 입력하세요"
          value={nickname}
          onChange={(e) => setNickname(e.target.value)}
          autoFocus
        />
      </Flex>
      <Flex gap="12px" alignItems="center">
        <Button variant="outline" size="full" onClick={handleCloseModal}>
          취소
        </Button>
        <Button size="full" disabled={!nickname.trim()} onClick={handleSubmitNickname}>
          참가하기
        </Button>
      </Flex>
    </Modal>
  );
};

const Img = styled.img`
  width: 100%;
  max-width: 250px;
  height: auto;
  margin: 0 auto;
  padding: 20px 0;
`;
