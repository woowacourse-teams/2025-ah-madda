import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { useQuestionForm } from '../hooks/useQuestionForm';
import { getErrorMessage } from '../utils/getErrorMessage';

import { QuestionItem } from './QuestionItem';

type QuestionFormProps = Omit<ReturnType<typeof useQuestionForm>, 'isValid'>;

export const QuestionForm = ({
  questions,
  addQuestion,
  deleteQuestion,
  updateQuestion,
}: QuestionFormProps) => {
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
        {(questions ?? []).map((question) => (
          <QuestionItem
            key={question.orderIndex}
            orderIndex={question.orderIndex}
            questionText={question.questionText}
            isRequired={question.isRequired}
            onDelete={() => deleteQuestion(question.orderIndex)}
            onChange={(updated) => updateQuestion(question.orderIndex, updated)}
            errorMessage={getErrorMessage('questions', question.questionText)}
          />
        ))}
      </Flex>
    </Card>
  );
};
