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

type Story = StoryObj<typeof meta>;

export const Basic: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'Basic usage of the Flex component with default properties (direction: row, align: flex-start, justify: flex-start).',
      },
    },
  },

  render: () => (
    <Flex>
      <div style={{ padding: '16px', backgroundColor: '#f0f0f0', border: '1px solid #ccc' }}>
        Item 1
      </div>

      <div style={{ padding: '16px', backgroundColor: '#f0f0f0', border: '1px solid #ccc' }}>
        Item 2
      </div>

      <div style={{ padding: '16px', backgroundColor: '#f0f0f0', border: '1px solid #ccc' }}>
        Item 3
      </div>
    </Flex>
  ),
};

export const Direction: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Demonstrates flex-direction property with row and column layouts.',
      },
    },
  },

  render: () => (
    <Flex direction="column" gap="16px">
      <div>
        <h4>Row Direction (default)</h4>

        <Flex gap="8px">
          <Flex
            justify="center"
            align="center"
            width="60px"
            height="60px"
            style={{ backgroundColor: '#e3f2fd', border: '1px solid #90caf9' }}
          >
            1
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="60px"
            height="60px"
            style={{ backgroundColor: '#e3f2fd', border: '1px solid #90caf9' }}
          >
            2
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="60px"
            height="60px"
            style={{ backgroundColor: '#e3f2fd', border: '1px solid #90caf9' }}
          >
            3
          </Flex>
        </Flex>
      </div>

      <div>
        <h4>Column Direction</h4>

        <Flex direction="column" gap="8px">
          <Flex
            justify="center"
            align="center"
            width="60px"
            height="60px"
            style={{ backgroundColor: '#e8f5e8', border: '1px solid #81c784' }}
          >
            1
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="60px"
            height="60px"
            style={{ backgroundColor: '#e8f5e8', border: '1px solid #81c784' }}
          >
            2
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="60px"
            height="60px"
            style={{ backgroundColor: '#e8f5e8', border: '1px solid #81c784' }}
          >
            3
          </Flex>
        </Flex>
      </div>
    </Flex>
  ),
};

export const Alignment: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'Shows different combinations of align-items and justify-content properties for controlling item alignment.',
      },
    },
  },

  render: () => (
    <Flex direction="column" gap="24px">
      <div>
        <h4>Center Alignment</h4>

        <Flex
          align="center"
          justify="center"
          height="120px"
          style={{ backgroundColor: '#f8f9fa', border: '1px dashed #dee2e6' }}
        >
          <Flex
            justify="center"
            align="center"
            padding="16px"
            style={{ backgroundColor: '#fff3e0', border: '1px solid #ffb74d' }}
          >
            Centered
          </Flex>
        </Flex>
      </div>

      <div>
        <h4>Space Between</h4>

        <Flex
          justify="space-between"
          align="center"
          height="80px"
          style={{ backgroundColor: '#f8f9fa', border: '1px dashed #dee2e6' }}
        >
          <Flex
            justify="center"
            align="center"
            padding="12px"
            style={{ backgroundColor: '#fce4ec', border: '1px solid #f06292' }}
          >
            Start
          </Flex>

          <Flex
            justify="center"
            align="center"
            padding="12px"
            style={{ backgroundColor: '#fce4ec', border: '1px solid #f06292' }}
          >
            Middle
          </Flex>

          <Flex
            justify="center"
            align="center"
            padding="12px"
            style={{ backgroundColor: '#fce4ec', border: '1px solid #f06292' }}
          >
            End
          </Flex>
        </Flex>
      </div>

      <div>
        <h4>Space Around</h4>

        <Flex
          justify="space-around"
          align="center"
          height="80px"
          style={{ backgroundColor: '#f8f9fa', border: '1px dashed #dee2e6' }}
        >
          <Flex
            justify="center"
            align="center"
            padding="12px"
            style={{ backgroundColor: '#e1f5fe', border: '1px solid #29b6f6' }}
          >
            1
          </Flex>

          <Flex
            justify="center"
            align="center"
            padding="12px"
            style={{ backgroundColor: '#e1f5fe', border: '1px solid #29b6f6' }}
          >
            2
          </Flex>

          <Flex
            justify="center"
            align="center"
            padding="12px"
            style={{ backgroundColor: '#e1f5fe', border: '1px solid #29b6f6' }}
          >
            3
          </Flex>
        </Flex>
      </div>
    </Flex>
  ),
};

export const Gap: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Demonstrates the gap property for controlling spacing between flex items.',
      },
    },
  },

  render: () => (
    <Flex direction="column" gap="20px">
      <div>
        <h4>No Gap</h4>

        <Flex>
          <Flex
            justify="center"
            align="center"
            width="80px"
            height="60px"
            style={{ backgroundColor: '#ffebee', border: '1px solid #ef5350' }}
          >
            1
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="80px"
            height="60px"
            style={{ backgroundColor: '#ffebee', border: '1px solid #ef5350' }}
          >
            2
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="80px"
            height="60px"
            style={{ backgroundColor: '#ffebee', border: '1px solid #ef5350' }}
          >
            3
          </Flex>
        </Flex>
      </div>

      <div>
        <h4>8px Gap</h4>

        <Flex gap="8px">
          <Flex
            justify="center"
            align="center"
            width="80px"
            height="60px"
            style={{ backgroundColor: '#e8f5e8', border: '1px solid #66bb6a' }}
          >
            1
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="80px"
            height="60px"
            style={{ backgroundColor: '#e8f5e8', border: '1px solid #66bb6a' }}
          >
            2
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="80px"
            height="60px"
            style={{ backgroundColor: '#e8f5e8', border: '1px solid #66bb6a' }}
          >
            3
          </Flex>
        </Flex>
      </div>

      <div>
        <h4>24px Gap</h4>

        <Flex gap="24px">
          <Flex
            justify="center"
            align="center"
            width="80px"
            height="60px"
            style={{ backgroundColor: '#e3f2fd', border: '1px solid #42a5f5' }}
          >
            1
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="80px"
            height="60px"
            style={{ backgroundColor: '#e3f2fd', border: '1px solid #42a5f5' }}
          >
            2
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="80px"
            height="60px"
            style={{ backgroundColor: '#e3f2fd', border: '1px solid #42a5f5' }}
          >
            3
          </Flex>
        </Flex>
      </div>
    </Flex>
  ),
};

export const FlexProperties: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Shows flex-grow, flex-shrink, and flex-basis properties in action.',
      },
    },
  },

  render: () => (
    <Flex direction="column" gap="20px">
      <div>
        <h4>Flex Grow</h4>

        <Flex gap="8px" width="400px" style={{ backgroundColor: '#f8f9fa', padding: '8px' }}>
          <Flex
            justify="center"
            align="center"
            padding="16px"
            style={{ backgroundColor: '#fff3e0', border: '1px solid #ffb74d' }}
          >
            Fixed
          </Flex>

          <Flex
            grow="1"
            justify="center"
            align="center"
            padding="16px"
            style={{ backgroundColor: '#e8f5e8', border: '1px solid #66bb6a' }}
          >
            Flex Grow 1
          </Flex>

          <Flex
            justify="center"
            align="center"
            padding="16px"
            style={{ backgroundColor: '#fff3e0', border: '1px solid #ffb74d' }}
          >
            Fixed
          </Flex>
        </Flex>
      </div>

      <div>
        <h4>Different Grow Values</h4>

        <Flex gap="8px" width="400px" style={{ backgroundColor: '#f8f9fa', padding: '8px' }}>
          <Flex
            grow="1"
            justify="center"
            align="center"
            padding="16px"
            style={{ backgroundColor: '#e3f2fd', border: '1px solid #42a5f5' }}
          >
            Grow 1
          </Flex>

          <Flex
            grow="2"
            justify="center"
            align="center"
            padding="16px"
            style={{ backgroundColor: '#fce4ec', border: '1px solid #ab47bc' }}
          >
            Grow 2
          </Flex>

          <Flex
            grow="1"
            justify="center"
            align="center"
            padding="16px"
            style={{ backgroundColor: '#e3f2fd', border: '1px solid #42a5f5' }}
          >
            Grow 1
          </Flex>
        </Flex>
      </div>
    </Flex>
  ),
};

export const Wrap: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Demonstrates flex-wrap property for controlling how items wrap to new lines.',
      },
    },
  },

  render: () => (
    <Flex direction="column" gap="20px">
      <div>
        <h4>No Wrap (default)</h4>

        <Flex gap="8px" width="300px" style={{ backgroundColor: '#f8f9fa', padding: '8px' }}>
          <Flex
            justify="center"
            align="center"
            width="100px"
            padding="16px"
            style={{ backgroundColor: '#ffebee', border: '1px solid #e57373' }}
          >
            Item 1
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="100px"
            padding="16px"
            style={{ backgroundColor: '#ffebee', border: '1px solid #e57373' }}
          >
            Item 2
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="100px"
            padding="16px"
            style={{ backgroundColor: '#ffebee', border: '1px solid #e57373' }}
          >
            Item 3
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="100px"
            padding="16px"
            style={{ backgroundColor: '#ffebee', border: '1px solid #e57373' }}
          >
            Item 4
          </Flex>
        </Flex>
      </div>

      <div>
        <h4>Wrap</h4>

        <Flex
          wrap="wrap"
          gap="8px"
          width="300px"
          style={{ backgroundColor: '#f8f9fa', padding: '8px' }}
        >
          <Flex
            justify="center"
            align="center"
            width="100px"
            padding="16px"
            style={{ backgroundColor: '#e8f5e8', border: '1px solid #81c784' }}
          >
            Item 1
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="100px"
            padding="16px"
            style={{ backgroundColor: '#e8f5e8', border: '1px solid #81c784' }}
          >
            Item 2
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="100px"
            padding="16px"
            style={{ backgroundColor: '#e8f5e8', border: '1px solid #81c784' }}
          >
            Item 3
          </Flex>

          <Flex
            justify="center"
            align="center"
            width="100px"
            padding="16px"
            style={{ backgroundColor: '#e8f5e8', border: '1px solid #81c784' }}
          >
            Item 4
          </Flex>
        </Flex>
      </div>
    </Flex>
  ),
};

export const Spacing: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Shows margin and padding properties for controlling external and internal spacing.',
      },
    },
  },

  render: () => (
    <Flex direction="column" gap="20px">
      <div>
        <h4>Margin</h4>

        <div style={{ backgroundColor: '#f8f9fa', padding: '8px' }}>
          <Flex
            margin="20px"
            justify="center"
            align="center"
            padding="16px"
            style={{ backgroundColor: '#fff', border: '1px solid #dee2e6' }}
          >
            Content with 20px margin
          </Flex>
        </div>
      </div>

      <div>
        <h4>Padding</h4>

        <Flex
          padding="20px"
          justify="center"
          align="center"
          style={{ backgroundColor: '#e9ecef', border: '1px solid #dee2e6' }}
        >
          <div style={{ backgroundColor: '#fff', padding: '8px', border: '1px solid #ccc' }}>
            Content with 20px padding
          </div>
        </Flex>
      </div>

      <div>
        <h4>Specific Margin/Padding</h4>

        <Flex
          marginTop="10px"
          marginBottom="20px"
          paddingLeft="30px"
          paddingRight="10px"
          justify="center"
          align="center"
          style={{ backgroundColor: '#e9ecef', border: '1px solid #dee2e6' }}
        >
          <div style={{ backgroundColor: '#fff', padding: '8px', border: '1px solid #ccc' }}>
            Custom spacing
          </div>
        </Flex>
      </div>
    </Flex>
  ),
};

export const Sizing: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Demonstrates width, height, max-width, and max-height properties.',
      },
    },
  },

  render: () => (
    <Flex direction="column" gap="20px">
      <div>
        <h4>Fixed Size</h4>

        <Flex
          width="300px"
          height="100px"
          justify="center"
          align="center"
          style={{ backgroundColor: '#e9ecef', border: '1px solid #dee2e6' }}
        >
          300px × 100px
        </Flex>
      </div>

      <div>
        <h4>Max Width</h4>

        <Flex
          maxWidth="200px"
          padding="16px"
          gap="8px"
          style={{ backgroundColor: '#e9ecef', border: '1px solid #dee2e6' }}
        >
          <Flex
            justify="center"
            align="center"
            padding="12px"
            style={{ backgroundColor: '#fff3e0', border: '1px solid #ffb74d' }}
          >
            max-width: 200px container
          </Flex>

          <Flex
            justify="center"
            align="center"
            padding="12px"
            style={{ backgroundColor: '#fff3e0', border: '1px solid #ffb74d' }}
          >
            Second item
          </Flex>
        </Flex>
      </div>
    </Flex>
  ),
};

export const ComplexLayout: Story = {
  parameters: {
    docs: {
      description: {
        story: 'A complex example combining multiple flex properties to create a practical layout.',
      },
    },
  },

  render: () => (
    <Flex direction="column" height="300px" style={{ border: '1px solid #dee2e6' }}>
      <Flex
        justify="space-between"
        align="center"
        padding="16px"
        style={{ backgroundColor: '#f8f9fa', borderBottom: '1px solid #dee2e6' }}
      >
        <h3 style={{ margin: 0 }}>Header</h3>

        <Flex gap="8px">
          <Flex
            justify="center"
            align="center"
            padding="8px 16px"
            style={{ backgroundColor: '#e3f2fd', border: '1px solid #90caf9' }}
          >
            Menu
          </Flex>

          <Flex
            justify="center"
            align="center"
            padding="8px 16px"
            style={{ backgroundColor: '#e3f2fd', border: '1px solid #90caf9' }}
          >
            Profile
          </Flex>
        </Flex>
      </Flex>

      <Flex grow="1">
        <Flex
          direction="column"
          width="200px"
          padding="16px"
          gap="8px"
          style={{ backgroundColor: '#f1f3f4', borderRight: '1px solid #dee2e6' }}
        >
          <Flex
            justify="center"
            align="center"
            padding="12px"
            style={{ backgroundColor: '#fff3e0', border: '1px solid #ffb74d' }}
          >
            Nav Item 1
          </Flex>

          <Flex
            justify="center"
            align="center"
            padding="12px"
            style={{ backgroundColor: '#fff3e0', border: '1px solid #ffb74d' }}
          >
            Nav Item 2
          </Flex>

          <Flex
            justify="center"
            align="center"
            padding="12px"
            style={{ backgroundColor: '#fff3e0', border: '1px solid #ffb74d' }}
          >
            Nav Item 3
          </Flex>
        </Flex>

        <Flex grow="1" direction="column" padding="16px" gap="16px">
          <h4 style={{ margin: 0 }}>Main Content</h4>

          <Flex wrap="wrap" gap="16px">
            <Flex
              justify="center"
              align="center"
              width="150px"
              height="100px"
              style={{ backgroundColor: '#e8f5e8', border: '1px solid #81c784' }}
            >
              Card 1
            </Flex>

            <Flex
              justify="center"
              align="center"
              width="150px"
              height="100px"
              style={{ backgroundColor: '#e8f5e8', border: '1px solid #81c784' }}
            >
              Card 2
            </Flex>

            <Flex
              justify="center"
              align="center"
              width="150px"
              height="100px"
              style={{ backgroundColor: '#e8f5e8', border: '1px solid #81c784' }}
            >
              Card 3
            </Flex>
          </Flex>
        </Flex>
      </Flex>

      <Flex
        justify="center"
        align="center"
        padding="16px"
        style={{ backgroundColor: '#f8f9fa', borderTop: '1px solid #dee2e6' }}
      >
        Footer Content
      </Flex>
    </Flex>
  ),
};
