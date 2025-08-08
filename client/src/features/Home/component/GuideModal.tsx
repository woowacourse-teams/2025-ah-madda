import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { ModalProps } from '@/shared/components/Modal/Modal';
import { Tabs } from '@/shared/components/Tabs';
import { Text } from '@/shared/components/Text';

import { TAB_DATA } from '../fixture/tab.data';

import { Information } from './Information';

type GuideModalProps = {
  onEnter: () => void;
} & ModalProps;

export const GuideModal = ({ isOpen, onClose, onEnter }: GuideModalProps) => {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      showCloseButton={false}
      css={css`
        width: clamp(300px, 50vw, 500px);
        max-height: 80vh;
        overflow-y: auto;
      `}
    >
      <Flex
        dir="column"
        gap="12px"
        width="100%"
        height="70%"
        css={css`
          overflow-y: auto;
        `}
      >
        <Text type="Heading" weight="bold">
          데스크톱 알람 설정 가이드
        </Text>

        <Tabs defaultValue="guide">
          <Tabs.List>
            <Tabs.Trigger value="guide">알림 설정 방법</Tabs.Trigger>
            <Tabs.Trigger value="setting">알림이 안 와요</Tabs.Trigger>
          </Tabs.List>
          <Tabs.Content value="guide">
            <Flex dir="column" gap="16px">
              {TAB_DATA.GUIDE.map((item, index) => (
                <Information
                  key={`guide-${index}`}
                  index={index + 1}
                  text={item.text}
                  imageUrl={item.imageUrl}
                />
              ))}
            </Flex>
          </Tabs.Content>

          <Tabs.Content value="setting">
            <Flex dir="column" gap="16px">
              {TAB_DATA.SETTING.map((item, index) => (
                <Information
                  key={`setting-${index}`}
                  index={index + 1}
                  text={item.text}
                  imageUrl={item.imageUrl}
                />
              ))}
            </Flex>
          </Tabs.Content>
        </Tabs>
        <Button size="full" onClick={onEnter}>
          이벤트 바로 가기
        </Button>
      </Flex>
    </Modal>
  );
};
