import { useState } from 'react';

import { Button } from '../../../../shared/components/Button';
import { Card } from '../../../../shared/components/Card';
import { Flex } from '../../../../shared/components/Flex';
import { Text } from '../../../../shared/components/Text';
import { QuestionRequest } from '../../types/Event';
import { VALIDATION_MESSAGES } from '../constants/validation';

import { QuestionItem } from './QuestionItem';

type QuestionFormProps = {
  questions: QuestionRequest[];
  onChange: (updated: QuestionRequest[]) => void;
  onErrorChange: (errors: Record<number, string>) => void;
};

export const QuestionForm = ({ questions, onChange, onErrorChange }: QuestionFormProps) => {
  const [questionErrors, setQuestionErrors] = useState<Record<number, string>>({});

  const addQuestion = () => {
    const newQuestions = [
      ...questions,
      { orderIndex: questions.length, isRequired: false, questionText: '' },
    ];
    onChange(newQuestions);
    const newError = {
      ...questionErrors,
      [questions.length]: VALIDATION_MESSAGES.REQUIRED('질문'),
    };
    setQuestionErrors(newError);
    onErrorChange(newError);
  };

  const deleteQuestion = (orderIndexToDelete: number) => {
    const updated = questions
      .filter((q) => q.orderIndex !== orderIndexToDelete)
      .map((q, index) => ({ ...q, orderIndex: index }));

    const updatedErrors: Record<number, string> = {};
    Object.entries(questionErrors).forEach(([key, value]) => {
      const i = Number(key);
      if (i !== orderIndexToDelete) {
        const newIndex = i > orderIndexToDelete ? i - 1 : i;
        updatedErrors[newIndex] = value;
      }
    });

    onChange(updated);
    setQuestionErrors(updatedErrors);
    onErrorChange(updatedErrors);
  };

  const updateQuestion = (orderIndex: number, updatedData: Partial<QuestionRequest>) => {
    const updated = questions.map((q) =>
      q.orderIndex === orderIndex ? { ...q, ...updatedData } : q
    );
    onChange(updated);

    const text = updated.find((q) => q.orderIndex === orderIndex)?.questionText ?? '';
    const errorMsg = text.trim() === '' ? VALIDATION_MESSAGES.REQUIRED('질문') : '';

    const updatedErrors = { ...questionErrors, [orderIndex]: errorMsg };
    setQuestionErrors(updatedErrors);
    onErrorChange(updatedErrors);
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
            errorMessage={questionErrors[question.orderIndex]}
          />
        ))}
      </Flex>
    </Card>
  );
};
