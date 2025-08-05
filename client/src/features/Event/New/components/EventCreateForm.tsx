import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';
import { useModal } from '@/shared/hooks/useModal';

import { UNLIMITED_CAPACITY } from '../constants/errorMessages';
import { useAddEvent } from '../hooks/useAddEvent';
import { useBasicEventForm } from '../hooks/useBasicEventForm';
import { useQuestionForm } from '../hooks/useQuestionForm';
import { convertDatetimeLocalToKSTISOString } from '../utils/convertDatetimeLocalToKSTISOString';

import { MaxCapacityModal } from './MaxCapacityModal';
import { QuestionForm } from './QuestionForm';

const ORGANIZATION_ID = 1; // 임시

export const EventCreateForm = () => {
  const navigate = useNavigate();
  const { mutate: addEvent } = useAddEvent(ORGANIZATION_ID);
  const { isOpen: isModalOpen, open, close } = useModal();

  const {
    basicForm,
    handleValueChange,
    validateField,
    handleChange,
    errors,
    isValid: isBasicFormValid,
  } = useBasicEventForm();

  const {
    questions,
    addQuestion,
    deleteQuestion,
    updateQuestion,
    isValid: isQuestionValid,
  } = useQuestionForm();

  const isFormReady = isBasicFormValid && isQuestionValid;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isBasicFormValid || !isQuestionValid) return;

    const payload = {
      ...basicForm,
      questions: questions,
      eventStart: convertDatetimeLocalToKSTISOString(basicForm.eventStart),
      eventEnd: convertDatetimeLocalToKSTISOString(basicForm.eventEnd),
      registrationEnd: convertDatetimeLocalToKSTISOString(basicForm.registrationEnd),
      organizerNickname: '임시닉',
    };

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
              name="title"
              label="이벤트 이름"
              value={basicForm.title}
              onChange={handleChange}
              errorMessage={errors.title}
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
                name="eventStart"
                label="이벤트 시작일"
                type="datetime-local"
                min="2025-07-31T14:00"
                placeholder="2025.07.30 13:00"
                value={basicForm.eventStart}
                onChange={(e) => {
                  handleChange(e);
                  const newValue = e.target.value;
                  handleValueChange('registrationEnd', newValue);
                  validateField('registrationEnd', newValue);
                }}
                errorMessage={errors.eventStart}
                isRequired
              />
              <Input
                id="eventEnd"
                name="eventEnd"
                label="이벤트 종료일"
                type="datetime-local"
                placeholder="2025.07.30 15:00"
                value={basicForm.eventEnd}
                min={basicForm.eventStart}
                onChange={handleChange}
                errorMessage={errors.eventEnd}
                isRequired
              />
            </Flex>

            <Input
              id="registrationEnd"
              name="registrationEnd"
              label="신청 종료일"
              type="datetime-local"
              placeholder="2025.07.25 15:00"
              value={basicForm.registrationEnd}
              max={basicForm.eventStart}
              onChange={handleChange}
              errorMessage={errors.registrationEnd}
              isRequired
            />

            <Input
              id="place"
              name="place"
              label="장소"
              placeholder="이벤트 장소를 입력해 주세요"
              value={basicForm.place}
              onChange={handleChange}
              errorMessage={errors.place}
            />

            <Input
              id="description"
              name="description"
              label="설명"
              placeholder="이벤트에 대한 설명을 입력해 주세요"
              value={basicForm.description}
              onChange={handleChange}
              errorMessage={errors.description}
            />

            <Flex
              height="45px"
              justifyContent="space-between"
              alignItems="center"
              padding="8px 12px"
              onClick={open}
              css={css`
                cursor: pointer;
                transition: background-color 0.2s;
                border-radius: 8px;
                &:hover {
                  background-color: #f5f5f5;
                }
              `}
            >
              <Flex alignItems="center" gap="8px">
                <Icon name="user" size={18} />
                <Text type="Label" color="gray">
                  수용 인원
                </Text>
                <Text type="Label">
                  {basicForm.maxCapacity === UNLIMITED_CAPACITY
                    ? '무제한'
                    : `${basicForm.maxCapacity}명`}
                </Text>
              </Flex>

              <Text type="Label" color="gray">
                ✏️
              </Text>
            </Flex>

            <MaxCapacityModal
              isOpen={isModalOpen}
              initialValue={
                basicForm.maxCapacity === UNLIMITED_CAPACITY ? 10 : basicForm.maxCapacity
              }
              onClose={close}
              onSubmit={(value) => {
                handleValueChange('maxCapacity', value);
                validateField('maxCapacity', value.toString());
              }}
            />
          </Flex>
        </Card>

        <QuestionForm
          questions={questions}
          addQuestion={addQuestion}
          deleteQuestion={deleteQuestion}
          updateQuestion={updateQuestion}
        />

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
