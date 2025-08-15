import type { Meta, StoryObj } from '@storybook/react';

import { Avatar } from './Avatar';

const meta = {
  title: 'components/Avatar',
  component: Avatar,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'Avatar component displays user profile images with name. A reusable component that receives data through props.',
      },
    },
  },
  argTypes: {
    picture: {
      control: 'text',
      description: 'Profile image URL',
    },
    name: {
      control: 'text',
      description: 'User name',
    },
  },
} satisfies Meta<typeof Avatar>;

export default meta;
type Story = StoryObj<typeof Avatar>;

export const Basic: Story = {
  args: {
    picture: 'https://ahmadda-dev.s3.ap-northeast-2.amazonaws.com/profile_avatar.png',
    name: '홍길동',
  },
};

export const WithInvalidImage: Story = {
  args: {
    picture: 'https://invalid-image-url',
    name: '홍길동',
  },
  parameters: {
    docs: {
      description: {
        story: 'When image loading fails with invalid URL, the default Ahmadda image is displayed',
      },
    },
  },
};

export const WithoutImage: Story = {
  args: {
    picture: null,
    name: '홍길동',
  },
  parameters: {
    docs: {
      description: {
        story: 'When no image URL is provided, the default Ahmadda image is displayed',
      },
    },
  },
};
