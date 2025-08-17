import { ReactNode, RefObject } from 'react';

import styled from '@emotion/styled';

type DatePickerContainerProps = {
  children: ReactNode;
  ref?: RefObject<HTMLDivElement | null>;
};

export const DatePickerContainer = ({ children, ref }: DatePickerContainerProps) => {
  return <StyledDatePickerDropdown ref={ref}>{children}</StyledDatePickerDropdown>;
};

const StyledDatePickerDropdown = styled.div`
  position: absolute;
  top: 100%;
  left: 0;
  background: ${({ theme }) => theme.colors.white};
  border: 1px solid ${({ theme }) => theme.colors.gray200};
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  padding: 20px;
  margin-top: 4px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
`;
