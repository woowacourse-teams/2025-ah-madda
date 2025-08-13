import { useRef, useState } from 'react';

import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

type Props = {
  onChange: (file: File | null) => void;
  accept?: string;
  initialPreviewUrl?: string;
  disabled?: boolean;
  label?: string;
};

export const OrganizationImageInput = ({
  onChange,
  accept = 'image/*',
  initialPreviewUrl,
  disabled,
  label = '조직 이미지',
}: Props) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const [preview, setPreview] = useState<string | null>(initialPreviewUrl ?? null);

  const openPicker = () => inputRef.current?.click();

  const setFile = (file: File | null) => {
    if (!file) {
      setPreview(null);
      onChange(null);
      return;
    }
    const url = URL.createObjectURL(file);
    setPreview(url);
    onChange(file);
  };

  const clearFile = () => setFile(null);

  return (
    <Flex
      dir="column"
      gap="12px"
      width="100%"
      css={css`
        max-width: 260px;
      `}
    >
      <Text type="Label">{label}</Text>

      <Button
        type="button"
        onClick={openPicker}
        disabled={disabled}
        aria-label="이미지 선택"
        css={css`
          width: 100%;
          aspect-ratio: 1 / 1;
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

      <Flex gap="8px">
        {preview && (
          <Button size="sm" type="button" color="secondary" onClick={clearFile} disabled={disabled}>
            제거
          </Button>
        )}
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
