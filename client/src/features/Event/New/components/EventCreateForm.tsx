import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';

import { useAddEvent } from '../hooks/useAddEvent';
import { useEventForm } from '../hooks/useEventForm';
import { useEventValidation } from '../hooks/useEventValidation';
import { convertDatetimeLocalToKSTISOString } from '../utils/convertDatetimeLocalToKSTISOString';

import { QuestionForm } from './QuestionForm';

const ORGANIZATION_ID = 1; // 임시
const ORGANIZER_NICKNAME = '임시닉네임'; // a.TODO: 추후 유저 설정 닉네임으로 대체

export const EventCreateForm = () => {
  const navigate = useNavigate();
  const { mutate: addEvent } = useAddEvent(ORGANIZATION_ID);
  const { formData, handleChange, setQuestions } = useEventForm();
  const { errors, validate, validateField, isFormValid } = useEventValidation(formData);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      ...formData,
      eventStart: convertDatetimeLocalToKSTISOString(formData.eventStart),
      eventEnd: convertDatetimeLocalToKSTISOString(formData.eventEnd),
      registrationEnd: convertDatetimeLocalToKSTISOString(formData.registrationEnd),
      organizerNickname: ORGANIZER_NICKNAME,
    };

    addEvent(payload, {
      onSuccess: ({ eventId }) => navigate(`/event/${eventId}`),
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
              onChange={(e) => {
                handleChange('title')(e);
                validateField('title', e.target.value);
              }}
              error={!!errors.title}
              errorMessage={errors.title}
            />

            <Flex gap="16px">
              <Input
                id="eventStart"
                label="이벤트 시작 날짜/시간"
                type="datetime-local"
                placeholder="2025.07.30 13:00"
                value={formData.eventStart}
                onChange={(e) => {
                  handleChange('eventStart')(e);
                  validateField('eventStart', e.target.value);
                }}
                error={!!errors.eventStart}
                errorMessage={errors.eventStart}
              />
              <Input
                id="eventEnd"
                label="이벤트 종료 날짜/시간"
                type="datetime-local"
                placeholder="2025.07.30 15:00"
                value={formData.eventEnd}
                onChange={(e) => {
                  handleChange('eventEnd')(e);
                  validateField('eventEnd', e.target.value);
                }}
                error={!!errors.eventEnd}
                errorMessage={errors.eventEnd}
              />
            </Flex>

            <Input
              id="registrationEnd"
              label="신청 종료 날짜/시간"
              type="datetime-local"
              placeholder="2025.07.25 15:00"
              value={formData.registrationEnd}
              onChange={(e) => {
                handleChange('registrationEnd')(e);
                validateField('registrationEnd', e.target.value);
              }}
              error={!!errors.registrationEnd}
              errorMessage={errors.registrationEnd}
            />

            <Input
              id="place"
              label="장소"
              placeholder="이벤트 장소를 입력해 주세요"
              value={formData.place}
              onChange={(e) => {
                handleChange('place')(e);
                validateField('place', e.target.value);
              }}
              error={!!errors.place}
              errorMessage={errors.place}
            />

            <Input
              id="description"
              label="설명"
              placeholder="이벤트에 대한 설명을 입력해 주세요"
              value={formData.description}
              onChange={(e) => {
                handleChange('description')(e);
                validateField('description', e.target.value);
              }}
              error={!!errors.description}
              errorMessage={errors.description}
            />

            <Input
              id="maxCapacity"
              label="수용 인원"
              placeholder="최대 참가 인원을 입력해 주세요"
              type="number"
              value={formData.maxCapacity.toString()}
              onChange={(e) => {
                handleChange('maxCapacity')(e);
                validateField('maxCapacity', e.target.value);
              }}
              error={!!errors.maxCapacity}
              errorMessage={errors.maxCapacity}
            />
          </Flex>
        </Card>

        <QuestionForm questions={formData.questions} onChange={setQuestions} />

        <Flex justifyContent="flex-end">
          <Button
            type="submit"
            color="black"
            size="sm"
            disabled={!isFormValid}
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
