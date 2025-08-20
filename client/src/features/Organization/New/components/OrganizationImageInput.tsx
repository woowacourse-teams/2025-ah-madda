import { useEffect, useRef, useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { StyledFooterRow, StyledHelperText } from '@/shared/components/Input/Input.styled';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

type OrganizationImageInputProps = {
  onChange: (file: File | null) => void;
  accept?: string;
  initialPreviewUrl?: string;
  disabled?: boolean;
  label?: string;
  errorMessage?: string;
  helperText?: string;
};

const ALLOWED_IMAGE_TYPE = 'image/png,image/jpeg,.png,.jpg,.jpeg';

export const OrganizationImageInput = ({
  onChange,
  accept = ALLOWED_IMAGE_TYPE,
  initialPreviewUrl,
  disabled,
  errorMessage,
  helperText,
}: OrganizationImageInputProps) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const [preview, setPreview] = useState<string | null>(initialPreviewUrl ?? null);

  useEffect(() => {
    return () => {
      if (preview?.startsWith('blob:')) URL.revokeObjectURL(preview);
    };
  }, [preview]);

  const resetInputValue = () => {
    if (inputRef.current) inputRef.current.value = '';
  };

  const openPicker = () => {
    resetInputValue();
    inputRef.current?.click();
  };

  const handleImageSelection = (file: File | null) => {
    const next = file ? URL.createObjectURL(file) : null;
    setPreview(next);
    onChange(file);
    resetInputValue();
  };

  const clearFile = () => handleImageSelection(null);

  const isError = Boolean(errorMessage);

  return (
    <Flex
      dir="column"
      gap="8px"
      width="100%"
      css={css`
        max-width: 260px;
      `}
      aria-invalid={isError || undefined}
    >
      <Flex width="255px" dir="column" gap="8px">
        <StyledImgUpload onClick={openPicker} disabled={disabled} aria-label="이미지 선택">
          {preview ? (
            <StyledPreviewImage src={preview} alt="선택한 이미지 미리보기" />
          ) : (
            <Text type="Label" color="gray">
              이미지 선택
            </Text>
          )}
        </StyledImgUpload>

        {preview && (
          <Flex justifyContent="flex-end">
            <Button
              size="sm"
              type="button"
              color="secondary"
              onClick={clearFile}
              disabled={disabled}
            >
              제거
            </Button>
          </Flex>
        )}
      </Flex>

      <Flex dir="column" width="255px">
        <StyledFooterRow>
          <StyledHelperText isError={isError}>
            {isError ? (errorMessage ?? ' ') : (helperText ?? ' ')}
          </StyledHelperText>
          <span />
        </StyledFooterRow>
      </Flex>

      <Input
        id="orgImage"
        ref={inputRef}
        type="file"
        accept={accept}
        onChange={(e) => handleImageSelection(e.target.files?.[0] ?? null)}
        css={css`
          display: none;
        `}
      />
    </Flex>
  );
};

const StyledPreviewImage = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
`;

const StyledImgUpload = styled(Button)`
  width: 255px;
  height: 255px;
  border: 1px dashed ${theme.colors.gray200};
  border-radius: 12px;
  background: ${theme.colors.gray50};
  padding: 0;
  overflow: hidden;
  place-items: center;

  &:hover {
    background: ${theme.colors.gray100};
  }
`;
