import type { Meta, StoryObj } from '@storybook/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

import { Avatar } from './Avatar';

const mockProfile = {
  id: 2,
  name: '홍길동',
  email: 'wjddks96@gmail.com',
  picture: 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png',
};

const createMockQueryClient = () => {
  return new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
        staleTime: Infinity,
        gcTime: Infinity,
      },
    },
  });
};

const meta = {
  title: 'components/Avatar',
  component: Avatar,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component: 'Avatar component displays user profile images with name on the left side.',
      },
    },
  },
} satisfies Meta<typeof Avatar>;

export default meta;
type Story = StoryObj<typeof Avatar>;

export const Basic: Story = {
  decorators: [
    (Story) => {
      const queryClient = createMockQueryClient();
      queryClient.setQueryData(['profile'], mockProfile);

      return (
        <QueryClientProvider client={queryClient}>
          <Story />
        </QueryClientProvider>
      );
    },
  ],
  render: () => <Avatar />,
};

export const WithInvalidImage: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Avatar with invalid image URL that will trigger handleImageError',
      },
    },
  },
  decorators: [
    (Story) => {
      const queryClient = createMockQueryClient();
      queryClient.setQueryData(['profile'], {
        ...mockProfile,
        picture: 'https://invalid-image-url',
      });

      return (
        <QueryClientProvider client={queryClient}>
          <Story />
        </QueryClientProvider>
      );
    },
  ],
  render: () => <Avatar />,
};
