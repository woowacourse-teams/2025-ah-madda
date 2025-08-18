import { useEffect, useRef, useState } from 'react';

import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { StyledFooterRow, StyledHelperText } from '@/shared/components/Input/Input.styled';
import { Text } from '@/shared/components/Text';

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

  const setFile = (file: File | null) => {
    const next = file ? URL.createObjectURL(file) : null;
    setPreview(next);
    onChange(file);
    resetInputValue();
  };

  const clearFile = () => setFile(null);

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
      <Flex
        width="255px"
        height="255px"
        css={css`
          position: relative;
          overflow: visible;
        `}
      >
        <Button
          onClick={openPicker}
          disabled={disabled}
          aria-label="이미지 선택"
          css={css`
            width: 255px;
            height: 255px;
            border: 1px dashed #d4d8e1;
            border-radius: 12px;
            background: #fafbfc;
            padding: 0;
            overflow: hidden;
            display: grid;
            place-items: center;

            &:hover {
              background: #f2f4f8;
            }

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
              display: block;
            }
          `}
        >
          {preview ? (
            <img src={preview} alt="선택한 이미지 미리보기" />
          ) : (
            <Text type="Label" color="gray">
              이미지 선택
            </Text>
          )}
        </Button>

        {preview && (
          <Button
            size="sm"
            type="button"
            color="secondary"
            onClick={clearFile}
            disabled={disabled}
            css={css`
              position: absolute;
              right: -85px;
              bottom: 0px;
            `}
          >
            제거
          </Button>
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

      <input
        ref={inputRef}
        type="file"
        accept={accept}
        style={{ display: 'none' }}
        onChange={(e) => setFile(e.target.files?.[0] ?? null)}
      />
    </Flex>
  );
};
