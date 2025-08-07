import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { useQuestionForm } from '../hooks/useQuestionForm';
import { getErrorMessage } from '../utils/getErrorMessage';

import { QuestionItem } from './QuestionItem';

type QuestionFormProps = Omit<ReturnType<typeof useQuestionForm>, 'isValid'> & {
  isEditable?: boolean;
};

export const QuestionForm = ({
  questions,
  addQuestion,
  deleteQuestion,
  updateQuestion,
  isEditable = true,
}: QuestionFormProps) => {
  return (
    <Card>
      <Flex dir="column" gap="16px">
        <Flex justifyContent="space-between" alignItems="center">
          <Text type="Body">사전 질문</Text>
          {isEditable ? (
            <Button
              size="md"
              color="tertiary"
              variant="outline"
              iconName="plus"
              onClick={addQuestion}
            >
              질문 추가
            </Button>
          ) : (
            <Text type="Label" color="gray">
              질문은 수정할 수 없습니다.
            </Text>
          )}
        </Flex>
        {isEditable && (
          <>
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
          </>
        )}
      </Flex>
    </Card>
  );
};
