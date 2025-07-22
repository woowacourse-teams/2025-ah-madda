import styled from '@emotion/styled';

export const StyledGoogleButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 12px 24px;
  border: 1px solid #dadce0;
  border-radius: 8px;
  background-color: #fff;
  color: #3c4043;
  font-family: 'Google Sans', Roboto, sans-serif;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  min-height: 48px;
  min-width: 200px;

  &:hover {
    box-shadow:
      0 1px 2px 0 rgba(60, 64, 67, 0.3),
      0 1px 3px 1px rgba(60, 64, 67, 0.15);
    border-color: #d2d6da;
  }

  &:focus {
    outline: none;
    box-shadow:
      0 1px 2px 0 rgba(60, 64, 67, 0.3),
      0 1px 3px 1px rgba(60, 64, 67, 0.15);
  }

  &:active {
    background-color: #f8f9fa;
    border-color: #dadce0;
    box-shadow: 0 1px 2px 0 rgba(60, 64, 67, 0.3);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;

    &:hover {
      box-shadow: none;
      border-color: #dadce0;
    }
  }
`;

export const GoogleIcon = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
`;

export const ButtonText = styled.span`
  color: #3c4043;
  font-weight: 500;
`;
