import { Card } from '../../../../shared/components/Card';
import { Flex } from '../../../../shared/components/Flex';
import { IconButton } from '../../../../shared/components/IconButton';
import { Input } from '../../../../shared/components/Input';
import { Switch } from '../../../../shared/components/Switch';
import { Text } from '../../../../shared/components/Text';
import { QuestionRequest } from '../../types/Event';

type QuestionItemProps = {
  onDelete: () => void;
  onChange: (updated: Partial<Pick<QuestionRequest, 'questionText' | 'isRequired'>>) => void;
} & QuestionRequest;

export const QuestionItem = ({
  orderIndex,
  questionText,
  isRequired,
  onDelete,
  onChange,
}: QuestionItemProps) => {
  const handleTextChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange({ questionText: e.target.value });
  };

  const handleSwitchChange = (checked: boolean) => {
    onChange({ isRequired: checked });
  };

  return (
    <Card>
      <Flex dir="column" gap="16px">
        <Input
          id={`question-${orderIndex}`}
          label={`질문${orderIndex + 1}`}
          placeholder="질문을 입력해주세요."
          value={questionText}
          onChange={handleTextChange}
        />
        <Input
          id="question"
          label="단답형 응답 필드"
          placeholder="참가자가 여기에 답변을 입력합니다."
          disabled
        />
        <Flex justifyContent="space-between" alignItems="center">
          <Flex as="label" justifyContent="center" alignItems="center" gap="8px" height="100%">
            <Switch
              id={`required-${orderIndex}`}
              checked={isRequired}
              onCheckedChange={handleSwitchChange}
            />
            <Text type="caption" color="gray">
              필수 질문
            </Text>
          </Flex>
          <IconButton name="delete" color="red" onClick={onDelete} />
        </Flex>
      </Flex>
    </Card>
  );
};
