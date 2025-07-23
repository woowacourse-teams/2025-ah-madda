import { useState } from 'react';

import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { IconButton } from '@/shared/components/IconButton';
import { Input } from '@/shared/components/Input';
import { Switch } from '@/shared/components/Switch';
import { Text } from '@/shared/components/Text';

type Props = {
  questionNumber: number;
  onDelete: () => void;
};

export const QuestionItem = ({ questionNumber, onDelete }: Props) => {
  const [selected, setSelected] = useState(false);

  const handleSwitchChange = () => {
    setSelected(!selected);
  };

  return (
    <Card>
      <Flex dir="column" gap="16px">
        <Input
          id={`question-${questionNumber}`}
          label={`질문${questionNumber}`}
          placeholder="질문을 입력해주세요."
        />
        <Input
          id="question"
          label="단답형 응답 필드"
          placeholder="참가자가 여기에 답변을 입력합니다."
          disabled
        />
        <Flex justifyContent="space-between" alignItems="center">
          <Flex as="label" justifyContent="center" alignItems="center" gap="8px" height="100%">
            <Switch id="switch" checked={selected} onCheckedChange={handleSwitchChange} />
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
