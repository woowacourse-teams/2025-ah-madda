import { useState } from 'react';

import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { Question } from '../../types/Event';

import { QuestionItem } from './QuestionItem';

export const QuestionForm = () => {
  const [questions, setQuestions] = useState<Question[]>([
    {
      orderIndex: 0,
      isRequired: false,
      questionText: '',
    },
  ]);

  const addQuestion = () => {
    const newOrderIndex = questions.length;
    setQuestions([
      ...questions,
      {
        orderIndex: newOrderIndex,
        isRequired: false,
        questionText: '',
      },
    ]);
  };

  const deleteQuestion = (orderIndexToDelete: number) => {
    if (questions.length <= 1) return;

    const updated = questions
      .filter((q) => q.orderIndex !== orderIndexToDelete)
      .map((q, index) => ({
        ...q,
        orderIndex: index,
      }));

    setQuestions(updated);
  };

  const updateQuestion = (orderIndex: number, updatedData: Partial<Question>) => {
    setQuestions((prev) =>
      prev.map((q) => (q.orderIndex === orderIndex ? { ...q, ...updatedData } : q))
    );
  };

  return (
    <Card>
      <Flex dir="column" gap="16px">
        <Flex justifyContent="space-between" alignItems="center">
          <Text type="caption">사전 질문</Text>
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
