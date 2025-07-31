import styled from '@emotion/styled';

export const MessageCard = ({ text }: { text: string }) => {
  return <MessageWrapper>{text}</MessageWrapper>;
};

export const MessageWrapper = styled.div`
  width: 100%;
  padding: 32px;
  background-color: #f8f9fa;
  white-space: pre-line;
  border-radius: 12px;
  font-weight: 500;
  font-size: 20px;
  color: #000;
`;
