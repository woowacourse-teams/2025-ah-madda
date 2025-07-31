import { useState } from 'react';

import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { HTTPError } from 'ky';
import { useNavigate } from 'react-router-dom';

import { myQueryOptions } from '@/api/queries/my';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';

import { UNLIMITED_CAPACITY } from '../constants/validation';
import { useAddEvent } from '../hooks/useAddEvent';
import { useEventForm } from '../hooks/useEventForm';
import { useEventValidation } from '../hooks/useEventValidation';
import { convertDatetimeLocalToKSTISOString } from '../utils/convertDatetimeLocalToKSTISOString';

import { MaxCapacityModal } from './MaxCapacityModal';
import { QuestionForm } from './QuestionForm';

const ORGANIZATION_ID = 1; // 임시
const ORGANIZER_NICKNAME = '임시닉네임';

export const EventCreateForm = () => {
  const navigate = useNavigate();
  const { mutate: addEvent } = useAddEvent(ORGANIZATION_ID);
  const { formData, handleChange, setQuestions } = useEventForm();
  const { errors, setQuestionErrors, validate, validateField, isFormValid } =
    useEventValidation(formData);
  const [isCapacityModalOpen, setIsCapacityModalOpen] = useState(false);
  const { data: userProfile } = useQuery(myQueryOptions.profile());

  const today = new Date();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      ...formData,
      eventStart: convertDatetimeLocalToKSTISOString(formData.eventStart),
      eventEnd: convertDatetimeLocalToKSTISOString(formData.eventEnd),
      registrationEnd: convertDatetimeLocalToKSTISOString(formData.registrationEnd),
      organizerNickname: userProfile?.name ?? ORGANIZER_NICKNAME,
    };

    addEvent(payload, {
      onSuccess: ({ eventId }) => {
        alert('😁 이벤트가 성공적으로 생성되었습니다!');
        navigate(`/event/${eventId}`);
      },
      onError: async (error) => {
        if (error instanceof HTTPError) {
          try {
            const errorData = await error.response.json();
            if (errorData.detail) {
              alert(`❌ ${errorData.detail}`);
            } else {
              alert('❌ 알 수 없는 에러가 발생했습니다.');
            }
          } catch {
            alert('❌ 에러 응답을 파싱할 수 없습니다.');
          }
        } else {
          alert(`❌ ${error instanceof Error ? error.message : '알 수 없는 에러입니다.'}`);
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
              error={!!errors.title}
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
                error={!!errors.eventStart}
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
                error={!!errors.eventEnd}
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
              error={!!errors.registrationEnd}
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
              error={!!errors.place}
              errorMessage={errors.place}
              isRequired={true}
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
              isRequired={true}
            />

            <div
              onClick={() => setIsCapacityModalOpen(true)}
              // eslint-disable-next-line react/no-unknown-property
              css={css`
                width: 100%;
                padding: 16px;
                border-radius: 10px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                cursor: pointer;

                &:hover {
                  background-color: #f0f0f0;
                }
              `}
            >
              <Flex alignItems="center" gap="8px">
                <Icon name="users" size={18} />
                <Text type="Label" color="gray">
                  수용 인원
                </Text>
              </Flex>

              <Flex alignItems="center" gap="4px">
                <Text type="Label">
                  {formData.maxCapacity === UNLIMITED_CAPACITY
                    ? '무제한'
                    : `${formData.maxCapacity}명`}
                </Text>
                <Text type="Label" color="gray">
                  ✏️
                </Text>
              </Flex>
            </div>

            <MaxCapacityModal
              isOpen={isCapacityModalOpen}
              initialValue={formData.maxCapacity === UNLIMITED_CAPACITY ? 10 : formData.maxCapacity}
              onClose={() => setIsCapacityModalOpen(false)}
              onSubmit={(value) => {
                const syntheticEvent = {
                  target: {
                    value: value.toString(),
                  },
                };
                handleChange('maxCapacity')(syntheticEvent as React.ChangeEvent<HTMLInputElement>);
                validateField('maxCapacity', syntheticEvent.target.value);
              }}
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
