import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';

import { useAddEvent } from '../hooks/useAddEvent';
import { useBasicEventForm } from '../hooks/useBasicEventForm';
import { useQuestionManager } from '../hooks/useQuestionManager';
import { convertDatetimeLocalToKSTISOString } from '../utils/convertDatetimeLocalToKSTISOString';

import { QuestionForm } from './QuestionForm';

const ORGANIZATION_ID = 1; // 임시

export const EventCreateForm = () => {
  const navigate = useNavigate();
  const { mutate: addEvent } = useAddEvent(ORGANIZATION_ID);

  const {
    basicForm,
    setValue,
    touchedMap,
    setTouched,
    validateField,
    errors,
    isValid: isBasicFormValid,
  } = useBasicEventForm();

  const questionManager = useQuestionManager();

  const isFormReady = isBasicFormValid && questionManager.isValid;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isBasicFormValid || !questionManager.isValid) return;

    const payload = {
      ...basicForm,
      questions: questionManager.questions,
      eventStart: convertDatetimeLocalToKSTISOString(basicForm.eventStart),
      eventEnd: convertDatetimeLocalToKSTISOString(basicForm.eventEnd),
      registrationEnd: convertDatetimeLocalToKSTISOString(basicForm.registrationEnd),
      organizerNickname: '임시닉',
    };

    console.log(payload);

    addEvent(payload, {
      onSuccess: ({ eventId }) => {
        alert('😁 이벤트가 성공적으로 생성되었습니다!');
        navigate(`/event/${eventId}`);
      },
      onError: (error) => {
        if (error instanceof HttpError) {
          return alert(
            error.data?.detail || '일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
          );
        }

        alert('네트워크 연결을 확인해주세요.');
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
              value={basicForm.title}
              onChange={(e) => {
                setValue('title', e.target.value);
                validateField('title', e.target.value);
              }}
              onBlur={() => setTouched('title')}
              errorMessage={touchedMap.title ? errors.title : ''}
              isRequired
            />

            <Flex
              dir="row"
              gap="16px"
              css={css`
                @media (max-width: 768px) {
                  flex-direction: column;
                }
              `}
            >
              <Input
                id="eventStart"
                label="이벤트 시작일"
                type="datetime-local"
                min="2025-07-31T14:00"
                placeholder="2025.07.30 13:00"
                value={basicForm.eventStart}
                onChange={(e) => {
                  const newValue = e.target.value;
                  setValue('eventStart', newValue);
                  validateField('eventStart', newValue);

                  setValue('registrationEnd', newValue);
                  validateField('registrationEnd', newValue);
                }}
                onBlur={() => setTouched('eventStart')}
                errorMessage={touchedMap.eventStart ? errors.eventStart : ''}
                isRequired
              />
              <Input
                id="eventEnd"
                label="이벤트 종료일"
                type="datetime-local"
                placeholder="2025.07.30 15:00"
                value={basicForm.eventEnd}
                min={basicForm.eventStart}
                onChange={(e) => {
                  setValue('eventEnd', e.target.value);
                  validateField('eventEnd', e.target.value);
                }}
                onBlur={() => setTouched('eventEnd')}
                errorMessage={touchedMap.eventEnd ? errors.eventEnd : ''}
                isRequired
              />
            </Flex>

            <Input
              id="registrationEnd"
              label="신청 종료일"
              type="datetime-local"
              placeholder="2025.07.25 15:00"
              value={basicForm.registrationEnd}
              max={basicForm.eventStart}
              onChange={(e) => {
                setValue('registrationEnd', e.target.value);
                validateField('registrationEnd', e.target.value);
              }}
              onBlur={() => setTouched('registrationEnd')}
              errorMessage={touchedMap.registrationEnd ? errors.registrationEnd : ''}
              isRequired
            />

            <Input
              id="place"
              label="장소"
              placeholder="이벤트 장소를 입력해 주세요"
              value={basicForm.place}
              onChange={(e) => {
                setValue('place', e.target.value);
                validateField('place', e.target.value);
              }}
              onBlur={() => setTouched('place')}
              errorMessage={touchedMap.place ? errors.place : ''}
              max={12}
            />

            <Input
              id="description"
              label="설명"
              placeholder="이벤트에 대한 설명을 입력해 주세요"
              value={basicForm.description}
              onChange={(e) => {
                setValue('description', e.target.value);
                validateField('description', e.target.value);
              }}
              onBlur={() => setTouched('description')}
              errorMessage={touchedMap.description ? errors.description : ''}
              max={80}
            />

            <Input
              id="maxCapacity"
              label="수용 인원"
              placeholder="최대 참가 인원을 입력해 주세요"
              type="number"
              value={basicForm.maxCapacity}
              min={1}
              onChange={(e) => setValue('maxCapacity', Number(e.target.value))}
              onBlur={() => setTouched('maxCapacity')}
              isRequired
            />
          </Flex>
        </Card>

        <QuestionForm manager={questionManager} />

        <Flex justifyContent="flex-end">
          <Button
            type="submit"
            color="black"
            size="sm"
            disabled={!isFormReady}
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
