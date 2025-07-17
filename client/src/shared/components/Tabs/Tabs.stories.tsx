import { css } from '@emotion/react';
import type { Meta, StoryObj } from '@storybook/react';

import { Tabs, TabsList, TabsTrigger, TabsContent } from './Tabs';

const meta = {
  title: 'components/Tabs',
  component: Tabs,
  tags: ['autodocs'],
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

export const CustomTabs: Story = {
  render: () => (
    <Tabs
      defaultValue="advanced1"
      css={css`
        padding: 24px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 16px;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        max-width: 600px;
        margin: 20px auto;

        &:hover {
          transform: translateY(-2px);
          transition: transform 0.3s ease;
        }
      `}
    >
      <TabsList
        css={css`
          background: rgba(255, 255, 255, 0.2);
          backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.3);
          padding: 6px;
          gap: 8px;
        `}
      >
        <TabsTrigger
          value="advanced1"
          css={css`
            color: white;
            font-weight: 600;
            padding: 12px 20px;
            border-radius: 8px;
            transition: all 0.3s ease;

            &:hover {
              background: rgba(255, 255, 255, 0.1);
              transform: scale(1.05);
            }

            &[data-active='true'] {
              background: white;
              color: #667eea;
              box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
            }
          `}
        >
          커스텀 탭 1
        </TabsTrigger>
        <TabsTrigger
          value="advanced2"
          css={css`
            color: white;
            font-weight: 600;
            padding: 12px 20px;
            border-radius: 8px;
            transition: all 0.3s ease;

            &:hover {
              background: rgba(255, 255, 255, 0.1);
              transform: scale(1.05);
            }

            &[data-active='true'] {
              background: white;
              color: #764ba2;
              box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
            }
          `}
        >
          커스텀 탭 2
        </TabsTrigger>
      </TabsList>

      <TabsContent
        value="advanced1"
        css={css`
          margin-top: 20px;
          padding: 24px;
          background: white;
          border-radius: 12px;
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        `}
      >
        <h3>커스텀 스타일 탭1</h3>
        <p>커스텀 스타일 탭은 커스텀 스타일을 적용할 수 있습니다.</p>
      </TabsContent>

      <TabsContent
        value="advanced2"
        css={css`
          margin-top: 20px;
          padding: 24px;
          background: white;
          border-radius: 12px;
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        `}
      >
        <h3>커스텀 스타일 탭2</h3>
        <p>커스텀 스타일 탭은 커스텀 스타일을 적용할 수 있습니다.</p>
      </TabsContent>
    </Tabs>
  ),
};
