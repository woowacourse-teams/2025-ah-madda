import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { QuestionRequest } from '../../types/Event';

import { QuestionItem } from './QuestionItem';

type QuestionFormProps = {
  questions: QuestionRequest[];
  onChange: (updated: QuestionRequest[]) => void;
};

export const QuestionForm = ({ questions, onChange }: QuestionFormProps) => {
  const addQuestion = () => {
    const newQuestions = [
      ...questions,
      {
        orderIndex: questions.length,
        isRequired: false,
        questionText: '',
      },
    ];
    onChange(newQuestions);
  };

  const deleteQuestion = (orderIndexToDelete: number) => {
    if (questions.length <= 1) return;

    const updated = questions
      .filter((q) => q.orderIndex !== orderIndexToDelete)
      .map((q, index) => ({
        ...q,
        orderIndex: index,
      }));

    onChange(updated);
  };

  const updateQuestion = (orderIndex: number, updatedData: Partial<QuestionRequest>) => {
    const updated = questions.map((q) =>
      q.orderIndex === orderIndex ? { ...q, ...updatedData } : q
    );
    onChange(updated);
  };

  return (
    <Card>
      <Flex dir="column" gap="16px">
        <Flex justifyContent="space-between" alignItems="center">
          <Text type="Label">사전 질문</Text>
          <Button
            width="100px"
            size="sm"
            color="black"
            fontColor="black"
            variant="outlined"
            onClick={addQuestion}
          >
            + 질문 추가
          </Button>
        </Flex>
        {questions.map((question) => (
          <QuestionItem
            key={question.orderIndex}
            orderIndex={question.orderIndex}
            questionText={question.questionText}
            isRequired={question.isRequired}
            onDelete={() => deleteQuestion(question.orderIndex)}
            onChange={(updated) => updateQuestion(question.orderIndex, updated)}
          />
        ))}
      </Flex>
    </Card>
  );
};
