import { useState } from 'react';

import type { Meta, StoryObj } from '@storybook/react';

import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { Dropdown } from './Dropdown';

const meta: Meta<typeof Dropdown> = {
  title: 'Components/Dropdown',
  component: Dropdown,
  parameters: {
    layout: 'centered',
    docs: {
      description: {
        component:
          '재사용 가능한 드롭다운 컴포넌트입니다. 트리거, 컨텐츠, 아이템으로 구성되어 있습니다.',
      },
    },
  },
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: function DefaultStory() {
    const [selectedOption, setSelectedOption] = useState('옵션을 선택하세요');

    return (
      <Flex dir="column" gap="16px">
        <Text type="Body">
          선택된 옵션: <strong>{selectedOption}</strong>
        </Text>

        <Dropdown>
          <Dropdown.Trigger>
            <Flex justifyContent="space-between" alignItems="center" width="100%" padding="8px">
              <Text type="Body" color={theme.colors.gray700}>
                {selectedOption}
              </Text>
              <Icon name="dropdownDown" size={16} color="gray500" />
            </Flex>
          </Dropdown.Trigger>

          <Dropdown.Content>
            <Dropdown.Item onClick={() => setSelectedOption('옵션 1')}>
              <Text type="Body">옵션 1</Text>
            </Dropdown.Item>
            <Dropdown.Item onClick={() => setSelectedOption('옵션 2')}>
              <Text type="Body">옵션 2</Text>
            </Dropdown.Item>
            <Dropdown.Item onClick={() => setSelectedOption('옵션 3')}>
              <Text type="Body">옵션 3</Text>
            </Dropdown.Item>
          </Dropdown.Content>
        </Dropdown>
      </Flex>
    );
  },
  parameters: {
    docs: {
      description: {
        story: '기본적인 드롭다운 사용법입니다. 옵션을 클릭하면 선택된 값이 표시됩니다.',
      },
    },
  },
};
