import { useState } from 'react';

import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { QuestionItem } from './QuestionItem';

export const QuestionForm = () => {
  const [questions, setQuestions] = useState([
    {
      questionNumber: 1,
      isRequired: false,
      placeholder: '질문을 입력해주세요.',
    },
  ]);

  const addQuestion = () => {
    const newQuestionNumber = Math.max(...questions.map((question) => question.questionNumber)) + 1;
    setQuestions([
      ...questions,
      {
        questionNumber: newQuestionNumber,
        isRequired: false,
        placeholder: '질문을 입력해주세요.',
      },
    ]);
  };

  const deleteQuestion = (questionNumber: number) => {
    if (questions.length <= 1) {
      return;
    }
    setQuestions(questions.filter((question) => question.questionNumber !== questionNumber));
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
            key={question.questionNumber}
            questionNumber={question.questionNumber}
            onDelete={() => deleteQuestion(question.questionNumber)}
          />
        ))}
      </Flex>
    </Card>
  );
};
