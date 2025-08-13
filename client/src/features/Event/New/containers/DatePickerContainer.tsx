import { ReactNode, forwardRef } from 'react';

import styled from '@emotion/styled';

type DatePickerContainerProps = {
  children: ReactNode;
};

export const DatePickerContainer = forwardRef<HTMLDivElement, DatePickerContainerProps>(
  ({ children }, ref) => {
    return <StyledDatePickerDropdown ref={ref}>{children}</StyledDatePickerDropdown>;
  }
);

DatePickerContainer.displayName = 'DatePickerContainer';

const StyledDatePickerDropdown = styled.div`
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
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
