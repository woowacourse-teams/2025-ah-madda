import type { Meta, StoryObj } from '@storybook/react';

import { Skeleton } from './Skeleton';

const meta = {
  title: 'components/Skeleton',
  component: Skeleton,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'The Skeleton component serves as a placeholder to indicate that content is loading, enhancing user experience during wait times.',
      },
    },
  },
} satisfies Meta<typeof Skeleton>;

export default meta;
type Story = StoryObj<typeof Skeleton>;

export const Basic: Story = {
  args: {
    width: '100%',
    height: '12px',
    borderRadius: '4px',
  },
  argTypes: {
    width: { control: 'text' },
    height: { control: 'text' },
    borderRadius: { control: 'text' },
  },
  render: () => <Skeleton {...Basic.args} />,
};

export const ImageSkeleton: Story = {
  args: {
    width: '200px',
    height: '200px',
    borderRadius: '8px',
  },
  argTypes: {
    width: { control: 'text' },
    height: { control: 'text' },
    borderRadius: { control: 'text' },
  },
  render: () => <Skeleton {...ImageSkeleton.args} />,
};
