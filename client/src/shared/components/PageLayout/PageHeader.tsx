import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';

import { Button } from '../Button';
import { Flex } from '../Flex';
import { Header } from '../Header';
import { Icon } from '../Icon';
import { IconButton } from '../IconButton';

export const PageHeader = () => {
  const navigate = useNavigate();

  const goHome = () => navigate(`/`);
  const goMyEvents = () => navigate(`/event/my`);
  const goProfile = () => navigate(`/profile`);

  const handleLogin = () => {
    const authUrl = getGoogleAuthUrl();
    window.location.href = authUrl;
    return;
  };

  return (
    <Header
      left={
        <Icon
          name="logo"
          size={55}
          onClick={goHome}
          css={css`
            cursor: pointer;
          `}
        />
      }
      right={
        isAuthenticated() ? (
          <Flex alignItems="center" gap="8px">
            <Button size="sm" onClick={goMyEvents}>
              내 이벤트
            </Button>
            <IconButton name="user" size={24} onClick={goProfile} />
          </Flex>
        ) : (
          <>
            <Button size="sm" onClick={handleLogin}>
              로그인
            </Button>
          </>
        )
      }
    />
  );
};
