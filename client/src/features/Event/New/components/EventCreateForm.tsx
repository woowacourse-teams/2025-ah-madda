import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { HTTPError } from 'ky';
import { useNavigate } from 'react-router-dom';

import { myQueryOptions } from '@/api/queries/my';
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

export const EventCreateForm = () => {
  const navigate = useNavigate();
  const { mutate: addEvent } = useAddEvent(ORGANIZATION_ID);
  const { formData, handleChange, setQuestions } = useEventForm();
  const { errors, setQuestionErrors, validate, validateField, isFormValid } =
    useEventValidation(formData);
  const { data: userProfile } = useQuery(myQueryOptions.profile());

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      ...formData,
      eventStart: convertDatetimeLocalToKSTISOString(formData.eventStart),
      eventEnd: convertDatetimeLocalToKSTISOString(formData.eventEnd),
      registrationEnd: convertDatetimeLocalToKSTISOString(formData.registrationEnd),
      organizerNickname: userProfile!.name,
    };

    addEvent(payload, {
      onSuccess: ({ eventId }) => {
        alert('😁 이벤트가 성공적으로 생성되었습니다!');
        navigate(`/event/${eventId}`);
      },
      onError: async (error) => {
        if (!(error instanceof HTTPError)) {
          return alert('네트워크 연결을 확인해주세요.');
        }

        try {
          const errorData = await error.response.json();
          return alert(
            `${errorData.detail || '일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'}`
          );
        } catch {
          alert('요청 처리 중 문제가 발생했습니다.');
        }
      },
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      <Flex dir="column" gap="20px" padding="60px 0" width="100%">
        <Text type="Title" weight="bold">
          새 이벤트 만들기
        </Text>
        <Text type="Body" color="gray">
          이벤트 정보를 입력해 주세요
        </Text>

        <Card>
          <Text type="Body">기본 질문</Text>
          <Flex dir="column">
            <Input
              id="title"
              label="이벤트 이름"
              value={formData.title}
              onChange={(e) => {
                handleChange('title')(e);
                validateField('title', e.target.value);
              }}
              errorMessage={errors.title}
              isRequired={true}
            />

            <Flex
              css={css`
                flex-wrap: wrap;

                @media (min-width: 600px) {
                  flex-wrap: nowrap;
                  gap: 16px;
                }

                > div {
                  flex: 1 1 100%;
                }

                @media (min-width: 600px) {
                  > div {
                    flex: 1;
                  }
                }
              `}
            >
              <Input
                id="eventStart"
                label="이벤트 시작일"
                type="datetime-local"
                min="2025-07-31T14:00"
                placeholder="2025.07.30 13:00"
                value={formData.eventStart}
                onChange={(e) => {
                  const newValue = e.target.value;

                  handleChange('eventStart')(e);
                  validateField('eventStart', newValue);

                  handleChange('registrationEnd')({
                    target: { value: newValue },
                  } as React.ChangeEvent<HTMLInputElement>);
                  validateField('registrationEnd', newValue);
                }}
                errorMessage={errors.eventStart}
                isRequired={true}
                step={600}
              />
              <Input
                id="eventEnd"
                label="이벤트 종료일"
                type="datetime-local"
                placeholder="2025.07.30 15:00"
                value={formData.eventEnd}
                min={formData.eventStart}
                onChange={(e) => {
                  handleChange('eventEnd')(e);
                  validateField('eventEnd', e.target.value);
                }}
                errorMessage={errors.eventEnd}
                isRequired={true}
              />
            </Flex>

            <Input
              id="registrationEnd"
              label="신청 종료일"
              type="datetime-local"
              placeholder="2025.07.25 15:00"
              value={formData.registrationEnd}
              max={formData.eventStart}
              onChange={(e) => {
                handleChange('registrationEnd')(e);
                validateField('registrationEnd', e.target.value);
              }}
              errorMessage={errors.registrationEnd}
              isRequired={true}
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
              errorMessage={errors.place}
              isRequired={true}
              max={12}
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
              errorMessage={errors.description}
              isRequired={true}
              max={80}
            />

            <Input
              id="maxCapacity"
              label="수용 인원"
              placeholder="최대 참가 인원을 입력해 주세요"
              type="number"
              value={formData.maxCapacity}
              min={1}
              onChange={handleChange('maxCapacity')}
              isRequired={true}
            />
          </Flex>
        </Card>

        <QuestionForm
          questions={formData.questions}
          onChange={setQuestions}
          onErrorChange={setQuestionErrors}
        />

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
