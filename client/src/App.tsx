import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useState } from 'react';

const App = () => {
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
      <h1>Hello World</h1>
      <button onClick={handleButtonClick}>Click me</button>
      <p>{count}</p>
      <h1>React + TypeScript + Webpack</h1>
    </Container>
  );
};

export default App;

const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
`;
