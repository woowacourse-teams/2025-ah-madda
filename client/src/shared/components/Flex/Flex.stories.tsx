import type { Meta, StoryObj } from '@storybook/react';

import { Flex } from './Flex';

const meta = {
  title: 'components/Flex',
  component: Flex,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'A flexible layout component built with CSS Flexbox. Provides comprehensive control over layout, alignment, spacing, and positioning.',
      },
    },
  },
} satisfies Meta<typeof Flex>;

export default meta;
type Story = StoryObj<typeof Flex>;

const SampleItem = ({
  children,
  color = '#e3f2fd',
}: {
  children: React.ReactNode;
  color?: string;
}) => (
  <div
    style={{
      padding: '16px',
      backgroundColor: color,
      border: '1px solid #90caf9',
      borderRadius: '4px',
      textAlign: 'center',
    }}
  >
    {children}
  </div>
);

export const Basic: Story = {
  args: {
    dir: 'row',
    justifyContent: 'flex-start',
    alignItems: 'stretch',
    gap: '16px',
    padding: '20px',
    width: '100%',
    height: 'auto',
  },
  argTypes: {
    as: { control: false },
    children: { control: false },
  },
  render: (args) => (
    <Flex {...args}>
      <SampleItem>Item 1</SampleItem>
      <SampleItem>Item 2</SampleItem>
      <SampleItem>Item 3</SampleItem>
    </Flex>
  ),
};
