import type { Meta, StoryObj } from '@storybook/react';

import { Icon } from '../Icon';

import { Tooltip } from './Tooltip';
import { PLACEMENT_TRANSFORMS } from './Tooltip.styled';

const meta = {
  title: 'components/Tooltip',
  component: Tooltip,
  tags: ['autodocs'],
} satisfies Meta<typeof Tooltip>;

export default meta;
type Story = StoryObj<typeof Tooltip>;

export const Basic: Story = {
  args: {
    placement: 'top',
    content: 'This is a basic tooltip component.',
  },
  argTypes: {
    placement: {
      control: 'select',
      options: Object.keys(PLACEMENT_TRANSFORMS),
    },
  },
  render: (args) => (
    <Tooltip {...args} content="This is a basic tooltip component.">
      <Icon name="setting" />
    </Tooltip>
  ),
};
