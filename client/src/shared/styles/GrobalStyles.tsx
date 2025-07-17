import { Global, css } from '@emotion/react';

const GlobalStyles = css`
  @font-face {
    font-family: 'Pretendard';
    font-style: normal;
    font-weight: 45 920;
    font-display: swap;
    src: url('/fonts/PretendardVariable.woff2') format('woff2-variations');
  }

  html {
    font-family: 'Pretendard';
  }
`;

export const GlobalStyle = () => <Global styles={GlobalStyles} />;
