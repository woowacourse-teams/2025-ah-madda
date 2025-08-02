import { useEffect, useState } from 'react';

import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { QuestionRequest } from '../../types/Event';
import { ERROR_MESSAGES } from '../constants/errorMessages';
import { useQuestionManager } from '../hooks/useQuestionManager';

import { QuestionItem } from './QuestionItem';

type QuestionFormProps = {
  questions: QuestionRequest[];
  onChange: (updated: QuestionRequest[]) => void;
  onErrorChange: (errors: Record<number, string>) => void;
};

export const QuestionForm = ({ questions, onChange, onErrorChange }: QuestionFormProps) => {
  const [questionErrors, setQuestionErrors] = useState<Record<number, string>>({});

  const { addQuestion, deleteQuestion, updateQuestion } = useQuestionManager(questions, onChange);

  useEffect(() => {
    const newErrors: Record<number, string> = {};

    questions.forEach((q, index) => {
      if (!q.questionText || q.questionText.trim() === '') {
        newErrors[index] = ERROR_MESSAGES.REQUIRED('질문');
      }
    });

    setQuestionErrors(newErrors);
    onErrorChange(newErrors);
  }, [questions, onErrorChange]);

  return (
    <Card>
      <Flex dir="column" gap="16px">
        <Flex justifyContent="space-between" alignItems="center">
          <Text type="Body">사전 질문</Text>
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
