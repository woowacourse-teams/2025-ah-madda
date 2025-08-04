import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';
import { colors } from '@/shared/styles/colors';

import { ErrorContainer } from '../containers/ErrorContainer';

type ErrorPageProps = {
  title?: string;
  message?: string;
};

export const ErrorPage = ({
  title = '문제가 발생했습니다',
  message = '예상치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
}: ErrorPageProps) => {
  const navigate = useNavigate();

  const handleGoHome = () => {
    navigate('/');
  };

  return (
    <PageLayout
      header={
        <Header left={<IconButton name="logo" size={55} onClick={() => navigate('/event')} />} />
      }
    >
      <ErrorContainer>
        <Flex dir="column" height="100%" justifyContent="center" alignItems="center" gap="24px">
          <Text type="Heading" weight="bold" color="#0A0A0A">
            {title}
          </Text>

          <Text
            css={css`
              font-size: 18px;
              color: ${colors.gray600};
              margin: 0;
              line-height: 1.6;
            `}
          >
            {message}
          </Text>

          <Flex dir="row" gap="16px" justifyContent="center" margin="24px 0 0 0">
            <Button
              size="lg"
              color="primary"
              onClick={handleGoHome}
              css={css`
                min-width: 140px;
              `}
            >
              홈으로 가기
            </Button>
          </Flex>
        </Flex>
      </ErrorContainer>
    </PageLayout>
  );
};
