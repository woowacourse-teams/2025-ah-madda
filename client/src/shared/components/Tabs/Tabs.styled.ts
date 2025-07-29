import styled from '@emotion/styled';

export const StyledTabs = styled.div`
  display: flex;
  flex-direction: column;
`;

export const StyledTabsList = styled.div`
  display: flex;
  border-radius: 12.75px;
  background-color: #ececf0;
  padding: 4px;
`;

export const StyledTabsTrigger = styled.button`
  width: 100%;
  border-radius: 10px;
  padding: 6px 12px;
  font-size: 14px;
  font-weight: 500;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #64748b;

  &:hover {
    color: #334155;
  }

  &[data-active='true'] {
    background-color: white;
    color: #0f172a;
    box-shadow:
      0 1px 3px 0 rgba(0, 0, 0, 0.1),
      0 1px 2px 0 rgba(0, 0, 0, 0.06);
  }
`;

export const StyledTabsContent = styled.div``;
