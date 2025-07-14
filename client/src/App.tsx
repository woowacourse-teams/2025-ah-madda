import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

export const App = () => {
  const [count, setCount] = useState(0);

  const handleButtonClick = () => {
    setCount(count + 1);
  };

  return (
    <Container
      css={css`
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100vh;
      `}
    >
      <button onClick={handleButtonClick}>Click me</button>
      <p>{count}</p>
      <h1>React + TypeScript + Webpack</h1>
    </Container>
  );
};

const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
`;
