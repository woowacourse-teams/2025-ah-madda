import { StyledLoadingText } from './Loading.styled';

export const Loading = () => {
  const text = 'Loading';
  return (
    <>
      {text.split('').map((char, i) => (
        <StyledLoadingText delay={i * 100} key={i}>
          {char}
        </StyledLoadingText>
      ))}
    </>
  );
};
