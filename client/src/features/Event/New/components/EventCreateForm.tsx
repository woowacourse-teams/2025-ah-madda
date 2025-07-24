import { useState } from 'react';

import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import type { CreateEventAPIRequest } from '@/features/Event/types/Event';

import { Button } from '../../../../shared/components/Button';
import { Card } from '../../../../shared/components/Card';
import { Flex } from '../../../../shared/components/Flex';
import { Input } from '../../../../shared/components/Input';
import { Text } from '../../../../shared/components/Text';
import { useAddEvent } from '../hooks/useAddEvent';
import { convertToISOString } from '../utils/convertToISOString';

import { QuestionForm } from './QuestionForm';

const ORGANIZATION_ID = 1; // 임시

export const EventCreateForm = () => {
  const navigate = useNavigate();
  const { mutate: addEvent } = useAddEvent(ORGANIZATION_ID);

  const [formData, setFormData] = useState<CreateEventAPIRequest>({
    title: '',
    eventStart: '',
    eventEnd: '',
    registrationStart: '',
    registrationEnd: '',
    place: '',
    description: '',
    organizerNickname: '',
    maxCapacity: 0,
    questions: [],
  });

  const handleChange =
    (key: keyof CreateEventAPIRequest) => (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = key === 'maxCapacity' ? Number(e.target.value) : e.target.value;
      setFormData((prev) => ({ ...prev, [key]: value }));
    };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const payload = {
      ...formData,
      eventStart: convertToISOString(formData.eventStart),
      eventEnd: convertToISOString(formData.eventEnd),
      registrationStart: convertToISOString(formData.registrationStart),
      registrationEnd: convertToISOString(formData.registrationEnd),
    };

    addEvent(payload, {
      onSuccess: ({ eventId }) => {
        navigate(`/event/${eventId}`);
      },
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      <Flex dir="column" gap="20px" padding="60px 0" width="100%">
        <Text type="Title" weight="bold">
          새 이벤트 만들기
        </Text>
        <Text type="caption" color="gray">
          이벤트 정보를 입력해 주세요
        </Text>

        <Card>
          <Text type="caption">기본 질문</Text>
          <Flex dir="column" gap="8px">
            <Input
              id="title"
              label="이벤트 이름"
              value={formData.title}
              onChange={handleChange('title')}
            />

            <Flex gap="16px">
              <Input
                id="eventStart"
                label="이벤트 시작 날짜/시간"
                placeholder="2025.07.30 13:00"
                value={formData.eventStart}
                onChange={handleChange('eventStart')}
              />
              <Input
                id="eventEnd"
                label="이벤트 종료 날짜/시간"
                placeholder="2025.07.30 15:00"
                value={formData.eventEnd}
                onChange={handleChange('eventEnd')}
              />
            </Flex>

            <Flex gap="16px">
              <Input
                id="registrationStart"
                label="신청 시작 날짜/시간"
                placeholder="2025.07.25 13:00"
                value={formData.registrationStart}
                onChange={handleChange('registrationStart')}
              />
              <Input
                id="registrationEnd"
                label="신청 종료 날짜/시간"
                placeholder="2025.07.25 15:00"
                value={formData.registrationEnd}
                onChange={handleChange('registrationEnd')}
              />
            </Flex>

            <Input
              id="place"
              label="장소"
              placeholder="이벤트 장소를 입력해 주세요"
              value={formData.place}
              onChange={handleChange('place')}
            />

            <Input
              id="description"
              label="설명"
              placeholder="이벤트에 대한 설명을 입력해 주세요"
              value={formData.description}
              onChange={handleChange('description')}
            />

            <Flex gap="16px">
              <Input
                id="author"
                label="주최자 이름"
                placeholder="주최자 이름을 입력해 주세요"
                value={formData.organizerNickname}
                onChange={handleChange('organizerNickname')}
              />
              <Input
                id="maxCapacity"
                label="수용 인원"
                placeholder="최대 참가 인원을 입력해 주세요"
                type="number"
                value={formData.maxCapacity.toString()}
                onChange={handleChange('maxCapacity')}
              />
            </Flex>
          </Flex>
        </Card>

        <QuestionForm
          questions={formData.questions}
          onChange={(newQuestions) => {
            setFormData((prev) => ({
              ...prev,
              questions: newQuestions,
            }));
          }}
        />

        <Flex justifyContent="flex-end">
          <Button
            type="submit"
            color="black"
            size="sm"
            css={css`
              border-radius: 5px;
              font-size: 12px;
              padding: 7px;
            `}
          >
            이벤트 만들기
          </Button>
        </Flex>
      </Flex>
    </form>
  );
};
