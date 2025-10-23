import { useState, useEffect } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import MobileDown from '@/assets/icon/mobile-down.webp';
import WebDown from '@/assets/icon/web-down.webp';

import { Button } from '../Button';
import { Flex } from '../Flex';
import { IconButton } from '../IconButton';
import { Modal, ModalProps } from '../Modal/Modal';
import { Text } from '../Text';

const CAROUSEL_DATA = [
  {
    image: MobileDown,
    description: '홈 화면에 앱을 추가하시면 더욱 편리하게 알림을 받을 수 있습니다.',
  },
  {
    image: WebDown,
    description: '앱을 다운로드 하면 더욱 편리하게 알림을 받을 수 있습니다.',
  },
];

export const LoginInfoModal = ({ isOpen, onClose }: ModalProps) => {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    if (!isOpen) return;

    const interval = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % CAROUSEL_DATA.length);
    }, 5000);

    return () => clearInterval(interval);
  }, [isOpen]);

  const goToSlide = (index: number) => {
    setCurrentIndex(index);
  };

  const nextSlide = () => {
    setCurrentIndex((prev) => (prev + 1) % CAROUSEL_DATA.length);
  };

  const prevSlide = () => {
    setCurrentIndex((prev) => (prev - 1 + CAROUSEL_DATA.length) % CAROUSEL_DATA.length);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      css={css`
        width: 480px;

        @media (max-width: 500px) {
          width: 90%;
        }
      `}
    >
      <Flex
        dir="column"
        alignItems="center"
        justifyContent="space-between"
        gap="10px"
        width="100%"
        padding="30px 0 0 0"
      >
        <StyledImageContainer>
          <StyledImage src={CAROUSEL_DATA[currentIndex].image} alt={`설명 ${currentIndex + 1}`} />
          <StyledNavButton onClick={prevSlide} position="left">
            <IconButton name="back" size={24} />
          </StyledNavButton>
          <StyledNavButton onClick={nextSlide} position="right">
            <IconButton name="next" size={24} />
          </StyledNavButton>
        </StyledImageContainer>

        <Flex dir="column" gap="10px" width="100%" alignItems="center" margin="20px 0 0 0">
          <Text type="Body" color="gray500">
            {CAROUSEL_DATA[currentIndex].description}
          </Text>

          <StyledIndicatorContainer>
            {CAROUSEL_DATA.map((_, index) => (
              <StyledIndicator
                key={index}
                isActive={index === currentIndex}
                onClick={() => goToSlide(index)}
              />
            ))}
          </StyledIndicatorContainer>

          <Button size="full" onClick={onClose}>
            확인
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};

const StyledImageContainer = styled.div`
  position: relative;
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
`;

const StyledImage = styled.img`
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: opacity 0.3s ease-in-out;
`;

const StyledNavButton = styled.button<{ position: 'left' | 'right' }>`
  position: absolute;
  top: 50%;
  ${({ position }) => position}: 12px;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease;
  background: transparent;
  z-index: 1;

  &:hover {
    background: rgba(0, 0, 0, 0.15);
  }

  &:active {
    transform: translateY(-50%) scale(0.95);
  }
`;

const StyledIndicatorContainer = styled.div`
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 16px;
`;

const StyledIndicator = styled.button<{ isActive: boolean }>`
  width: 8px;
  height: 8px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  background: ${({ isActive }) => (isActive ? '#007bff' : '#dee2e6')};
  transition: background 0.2s ease;

  &:hover {
    background: ${({ isActive }) => (isActive ? '#0056b3' : '#adb5bd')};
  }
`;
