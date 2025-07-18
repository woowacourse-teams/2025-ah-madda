import type { Meta, StoryObj } from '@storybook/react';

import { Tabs, TabsList, TabsTrigger, TabsContent } from './Tabs';

const meta = {
  title: 'components/Tabs',
  component: Tabs,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'A flexible and accessible tabs component for organizing content into separate panels. ',
      },
    },
  },
} satisfies Meta<typeof Tabs>;

export default meta;
type Story = StoryObj<typeof Tabs>;

export const Basic: Story = {
  render: () => (
    <Tabs defaultValue="tab1">
      <TabsList>
        <TabsTrigger value="tab1">탭 1</TabsTrigger>
        <TabsTrigger value="tab2">탭 2</TabsTrigger>
      </TabsList>
      <TabsContent value="tab1">
        <p>첫 번째 탭의 내용입니다.</p>
      </TabsContent>
      <TabsContent value="tab2">
        <p>두 번째 탭의 내용입니다.</p>
      </TabsContent>
    </Tabs>
  ),
};
