import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';
import { colors } from '@/shared/styles/colors';

import { ErrorContainer } from '../containers/ErrorContainer';

export const ErrorPage = () => {
  const navigate = useNavigate();

  return (
    <PageLayout
      header={
        <Header
          left={
            <Icon
              name="logo"
              size={55}
              onClick={() => navigate(`/`)}
              css={css`
                cursor: pointer;
              `}
            />
          }
        />
      }
    >
      <ErrorContainer>
        <Flex dir="column" height="100%" justifyContent="center" alignItems="center" gap="24px">
          <Text type="Heading" weight="bold" color="#0A0A0A">
            문제가 발생했습니다
          </Text>

          <Flex dir="column" alignItems="center" justifyContent="center" gap="8px">
            <Text type="Body" weight="medium" color={colors.gray600}>
              예상치 못한 오류가 발생했습니다.
            </Text>
            <Text type="Body" weight="medium" color={colors.gray600}>
              잠시 후 다시 시도해주세요.
            </Text>
          </Flex>

          <Flex dir="row" gap="16px" justifyContent="center" margin="24px 0 0 0">
            <Button size="lg" color="primary" onClick={() => navigate('/')}>
              홈으로 가기
            </Button>
          </Flex>
        </Flex>
      </ErrorContainer>
    </PageLayout>
  );
};
