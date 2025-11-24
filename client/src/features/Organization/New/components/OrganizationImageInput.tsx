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

  const previewBlobRef = useRef<string | null>(null);

  useEffect(() => {
    if (previewBlobRef.current) return;
    setPreview(initialPreviewUrl ?? null);
  }, [initialPreviewUrl]);

  useEffect(() => {
    return () => {
      if (previewBlobRef.current) {
        URL.revokeObjectURL(previewBlobRef.current);
        previewBlobRef.current = null;
      }
    };
  }, []);

  const resetInputValue = () => {
    if (inputRef.current) inputRef.current.value = '';
  };

  const openPicker = () => {
    resetInputValue();
    inputRef.current?.click();
  };

  const handleImageSelection = (file: File | null) => {
    if (previewBlobRef.current) {
      URL.revokeObjectURL(previewBlobRef.current);
      previewBlobRef.current = null;
    }

    if (!file) {
      setPreview(initialPreviewUrl ?? null);
      onChange(null);
      resetInputValue();
      return;
    }

    const next = URL.createObjectURL(file);
    previewBlobRef.current = next;
    setPreview(next);
    onChange(file);
    resetInputValue();
  };

  const clearFile = () => handleImageSelection(null);

  const isError = Boolean(errorMessage);
  const isEditMode = Boolean(initialPreviewUrl);
  const hasNewFile = Boolean(previewBlobRef.current);
  const showRemoveButton = isEditMode ? hasNewFile : Boolean(preview);

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
        <StyledImgBox>
          <StyledImgUpload onClick={openPicker} disabled={disabled} aria-label="이미지 선택">
            {preview ? (
              <StyledPreviewImage src={preview} alt="선택한 이미지 미리보기" />
            ) : (
              <Text type="Label" color="gray">
                이미지 선택
              </Text>
            )}
          </StyledImgUpload>

          {showRemoveButton && (
            <StyledRemoveButton
              size="sm"
              type="button"
              color="secondary"
              onClick={(e) => {
                e.stopPropagation();
                clearFile();
              }}
              disabled={disabled}
            >
              {isEditMode ? '복구' : '제거'}
            </StyledRemoveButton>
          )}
        </StyledImgBox>
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
  object-fit: scale-down;
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

const StyledImgBox = styled.div`
  position: relative;
  width: 255px;
  height: 255px;
`;

const StyledRemoveButton = styled(Button)`
  position: absolute;
  right: 8px;
  bottom: 8px;
  z-index: 1;
`;
