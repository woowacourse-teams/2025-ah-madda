import { css } from '@emotion/react';

import { theme } from '@/shared/styles/theme';

import { Flex } from '../Flex';
import { Icon } from '../Icon';
import { Spacing } from '../Spacing';
import { Text } from '../Text';

import { StyledFooterContainer, StyledLinkButton } from './Footer.styled';

export const Footer = () => {
  return (
    <StyledFooterContainer>
      <Flex
        justifyContent="center"
        margin="0 auto"
        padding="20px"
        css={css`
          max-width: 1120px;
        `}
      >
        <Flex dir="column" width="100%" gap="10px">
          <Flex alignItems="center">
            <img src="/favicon-light.png" alt="logo" width="30" height="30" />
            <Icon name="logo" />
          </Flex>
          <Text type="Body" weight="semibold" color={theme.colors.gray600}>
            우아한테크코스 - 아맞다팀
          </Text>
          <Spacing height="10px" />
          <Text type="Label" color={theme.colors.gray600}>
            {`Copyright ⓒ ahmadda.\nAll Rights Reserved E-mail: amadda.team@gmail.com`}
          </Text>
        </Flex>
        <Flex justifyContent="center" alignItems="center" gap="8px" height="40px">
          <StyledLinkButton
            target="_blank"
            href="https://pleasant-goat-041.notion.site/24ff55e0580c80c09bcfc5ce5da493f5?source=copy_link"
          >
            <Text type="Body" weight="semibold" color={theme.colors.gray600}>
              팀 소개
            </Text>
          </StyledLinkButton>
          <StyledLinkButton
            target="_blank"
            href="https://github.com/woowacourse-teams/2025-ah-madda"
          >
            <img src="/github.png" alt="github" width="30" height="30" />
          </StyledLinkButton>
        </Flex>
      </Flex>
    </StyledFooterContainer>
  );
};
