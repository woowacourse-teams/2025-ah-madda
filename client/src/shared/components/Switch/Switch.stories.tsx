import { useState } from 'react';

import type { Meta, StoryObj } from '@storybook/react';

import { Switch } from './Switch';

const meta = {
  title: 'shared/components/Switch',
  component: Switch,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'A modern toggle switch component for boolean states. Provides a sleek alternative to checkboxes with smooth animations and full accessibility support.',
      },
    },
    layout: 'centered',
  },
  argTypes: {
    checked: {
      control: { type: 'boolean' },
      description: 'Whether the switch is checked/on',
      table: {
        type: { summary: 'boolean' },
        defaultValue: { summary: 'false' },
      },
    },
    disabled: {
      control: { type: 'boolean' },
      description: 'Whether the switch is disabled',
      table: {
        type: { summary: 'boolean' },
        defaultValue: { summary: 'false' },
      },
    },
    onCheckedChange: {
      action: 'toggled',
      description: 'Callback function called when switch state changes',
      table: {
        type: { summary: '(checked: boolean) => void' },
      },
    },
    id: {
      control: { type: 'text' },
      description: 'HTML id attribute for the switch (useful for labels)',
      table: {
        type: { summary: 'string' },
      },
    },
  },
} satisfies Meta<typeof Switch>;

export default meta;
type Story = StoryObj<typeof Switch>;

// Basic Stories
export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Default toggle switch in unchecked state.',
      },
    },
  },
  args: {
    checked: false,
    onCheckedChange: (checked) => console.log('Switch toggled to:', checked),
  },
};

export const Checked: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Toggle switch in checked/active state.',
      },
    },
  },
  args: {
    checked: true,
    onCheckedChange: (checked) => console.log('Switch toggled to:', checked),
  },
};

export const Disabled: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Disabled toggle switch that cannot be interacted with.',
      },
    },
  },
  args: {
    checked: false,
    disabled: true,
    onCheckedChange: (checked) => console.log('Switch toggled to:', checked),
  },
};

export const DisabledChecked: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Disabled toggle switch in checked state.',
      },
    },
  },
  args: {
    checked: true,
    disabled: true,
    onCheckedChange: (checked) => console.log('Switch toggled to:', checked),
  },
};

const QuestionFormBuilderExample = () => {
  const [questions, setQuestions] = useState([
    { id: '1', question: 'What is your full name?', required: true },
    { id: '2', question: 'What is your email address?', required: true },
    { id: '3', question: 'What is your phone number?', required: false },
    { id: '4', question: 'Any additional comments?', required: false },
  ]);

  const updateQuestion = (id: string, field: 'question' | 'required', value: string | boolean) => {
    setQuestions((prev) => prev.map((q) => (q.id === id ? { ...q, [field]: value } : q)));
  };

  const addQuestion = () => {
    const newId = String(questions.length + 1);
    setQuestions((prev) => [
      ...prev,
      {
        id: newId,
        question: 'New question...',
        required: false,
      },
    ]);
  };

  const removeQuestion = (id: string) => {
    setQuestions((prev) => prev.filter((q) => q.id !== id));
  };

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '16px',
        maxWidth: '500px',
        padding: '20px',
        border: '1px solid #e5e7eb',
        borderRadius: '8px',
      }}
    >
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h3 style={{ margin: 0, fontSize: '18px', fontWeight: '600' }}>Form Builder</h3>
        <button
          onClick={addQuestion}
          style={{
            padding: '8px 16px',
            fontSize: '14px',
            backgroundColor: '#3b82f6',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
          }}
        >
          Add Question
        </button>
      </div>

      {questions.map((question, index) => (
        <div
          key={question.id}
          style={{
            padding: '16px',
            border: '1px solid #d1d5db',
            borderRadius: '6px',
            backgroundColor: '#f9fafb',
            display: 'flex',
            flexDirection: 'column',
            gap: '12px',
          }}
        >
          <div
            style={{
              display: 'flex',
              justifyContent: 'between',
              alignItems: 'center',
              gap: '12px',
            }}
          >
            <span style={{ fontSize: '12px', color: '#6b7280', minWidth: '60px' }}>
              Q{index + 1}
            </span>
            <input
              type="text"
              value={question.question}
              onChange={(e) => updateQuestion(question.id, 'question', e.target.value)}
              style={{
                flex: 1,
                padding: '8px 12px',
                border: '1px solid #d1d5db',
                borderRadius: '4px',
                fontSize: '14px',
              }}
            />
            {questions.length > 1 && (
              <button
                onClick={() => removeQuestion(question.id)}
                style={{
                  padding: '4px 8px',
                  fontSize: '12px',
                  backgroundColor: '#ef4444',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                }}
              >
                Remove
              </button>
            )}
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Switch
              id={`required-${question.id}`}
              checked={question.required}
              onCheckedChange={(checked) => updateQuestion(question.id, 'required', checked)}
            />
            <label
              htmlFor={`required-${question.id}`}
              style={{ fontSize: '14px', cursor: 'pointer' }}
            >
              필수 질문
            </label>
            {question.required && <span style={{ fontSize: '12px', color: '#ef4444' }}>*</span>}
          </div>
        </div>
      ))}
    </div>
  );
};

const AccessibilityExample = () => {
  const [features, setFeatures] = useState({
    screenReader: true,
    highContrast: false,
    largeText: true,
    reducedMotion: false,
  });

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '16px',
        maxWidth: '400px',
        padding: '20px',
        border: '1px solid #e5e7eb',
        borderRadius: '8px',
      }}
    >
      <h3 style={{ margin: '0 0 8px 0', fontSize: '18px', fontWeight: '600' }}>
        Accessibility Settings
      </h3>
      <p style={{ margin: '0 0 16px 0', fontSize: '14px', color: '#6b7280' }}>
        Try using Tab and Space keys to navigate and toggle switches.
      </p>

      {Object.entries(features).map(([key, value]) => (
        <div key={key} style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Switch
            id={`accessibility-${key}`}
            checked={value}
            onCheckedChange={(checked) => setFeatures((prev) => ({ ...prev, [key]: checked }))}
          />
          <label
            htmlFor={`accessibility-${key}`}
            style={{
              fontSize: '14px',
              cursor: 'pointer',
              flex: 1,
            }}
          >
            {key.replace(/([A-Z])/g, ' $1').replace(/^./, (str) => str.toUpperCase())}
          </label>
        </div>
      ))}
    </div>
  );
};

export const QuestionFormBuilder: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'Real-world example matching the provided Figma interface - dynamic question form builder with required field toggles.',
      },
    },
  },
  render: () => <QuestionFormBuilderExample />,
};
