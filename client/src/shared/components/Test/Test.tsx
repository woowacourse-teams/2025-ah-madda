import { StyledTest } from './Test.styled';

type TestProps = {
  title?: string;
  variant?: 'primary' | 'secondary';
  children?: React.ReactNode;
};

export const Test = ({ title = 'Test Component', variant = 'primary', children }: TestProps) => {
  return (
    <StyledTest variant={variant}>
      <h3>{title}</h3>
      {children && <div>{children}</div>}
    </StyledTest>
  );
};
