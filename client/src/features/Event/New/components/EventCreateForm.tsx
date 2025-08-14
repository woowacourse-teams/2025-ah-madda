import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { useAddTemplate } from '@/api/mutations/useAddTemplate';
import { useUpdateEvent } from '@/api/mutations/useUpdateEvent';
import { getEventDetailAPI } from '@/api/queries/event';
import type { EventTemplateAPIResponse, TemplateDetailAPIResponse } from '@/api/types/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';
import { Textarea } from '@/shared/components/Textarea';
import { useModal } from '@/shared/hooks/useModal';
import { trackCreateEvent } from '@/shared/lib/gaEvents';
import { theme } from '@/shared/styles/theme';

import { MAX_LENGTH, UNLIMITED_CAPACITY } from '../constants/errorMessages';
import { useAddEvent } from '../hooks/useAddEvent';
import { useBasicEventForm } from '../hooks/useBasicEventForm';
import { useQuestionForm } from '../hooks/useQuestionForm';
import { convertDatetimeLocalToKSTISOString } from '../utils/convertDatetimeLocalToKSTISOString';

import { MaxCapacityModal } from './MaxCapacityModal';
import { QuestionForm } from './QuestionForm';
import { TemplateModal } from './TemplateModal';

const ORGANIZATION_ID = 1; // 임시

type EventCreateFormProps = {
  isEdit: boolean;
  eventId?: number;
};

export const EventCreateForm = ({ isEdit, eventId }: EventCreateFormProps) => {
  const navigate = useNavigate();
  const { mutate: addEvent } = useAddEvent(ORGANIZATION_ID);
  const { mutate: updateEvent } = useUpdateEvent();
  const { mutate: loadPastEvent } = useAddTemplate();

  const { data: eventDetail } = useQuery({
    queryKey: ['event', 'detail', Number(eventId)],
    queryFn: () => getEventDetailAPI(Number(eventId)),
    enabled: isEdit,
  });

  const {
    isOpen: isTemplateModalOpen,
    open: templateModalOpen,
    close: templateModalClose,
  } = useModal();
  const {
    isOpen: isCapacityModalOpen,
    open: capacityModalOpen,
    close: capacityModalClose,
  } = useModal();

  const {
    basicEventForm,
    handleValueChange,
    validateField,
    handleChange,
    errors,
    isValid: isBasicFormValid,
    loadFormData,
  } = useBasicEventForm(isEdit ? eventDetail : undefined);

  const {
    questions,
    addQuestion,
    deleteQuestion,
    updateQuestion,
    isValid: isQuestionValid,
  } = useQuestionForm();

  const isFormReady = isBasicFormValid && isQuestionValid;

  const handleTemplateSelected = (
    templateDetail: Pick<TemplateDetailAPIResponse, 'description'>
  ) => {
    loadFormData({
      title: '',
      description: templateDetail.description,
      place: '',
      maxCapacity: UNLIMITED_CAPACITY,
    });
    alert('템플릿이 성공적으로 불러와졌습니다!');
  };

  const handleEventSelected = (eventData: Omit<EventTemplateAPIResponse, 'eventId'>) => {
    loadFormData({
      title: eventData.title,
      description: eventData.description,
      place: eventData.place,
      maxCapacity: eventData.maxCapacity,
    });
    alert('이벤트가 성공적으로 불러와졌습니다!');
  };

  const handleError = (error: unknown) => {
    if (error instanceof HttpError) {
      alert(error.data?.detail || '일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      return;
    }
    alert('네트워크 연결을 확인해주세요.');
  };

  const buildPayload = () => ({
    ...basicEventForm,
    questions,
    eventStart: convertDatetimeLocalToKSTISOString(basicEventForm.eventStart),
    eventEnd: convertDatetimeLocalToKSTISOString(basicEventForm.eventEnd),
    registrationEnd: convertDatetimeLocalToKSTISOString(basicEventForm.registrationEnd),
  });

  const submitCreate = (payload: ReturnType<typeof buildPayload>) => {
    addEvent(payload, {
      onSuccess: ({ eventId }) => {
        trackCreateEvent();
        alert('😁 이벤트가 성공적으로 생성되었습니다!');
        navigate(`/event/${eventId}`);
      },
      onError: handleError,
    });
  };

  const submitUpdate = (eventId: number, payload: ReturnType<typeof buildPayload>) => {
    updateEvent(
      { eventId, payload },
      {
        onSuccess: () => {
          alert('😁 이벤트가 성공적으로 수정되었습니다!');
          navigate(`/event/${eventId}`);
        },
        onError: handleError,
      }
    );
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isBasicFormValid || !isQuestionValid) return;

    const payload = buildPayload();

    if (isEdit && eventId) {
      submitUpdate(eventId, payload);
    } else {
      submitCreate(payload);
    }
  };

  const handleLoadPastEvent = () => {
    loadPastEvent(
      {
        title: basicEventForm.title,
        description: basicEventForm.description,
      },
      {
        onSuccess: () => {
          alert('템플릿이 성공적으로 추가되었습니다!');
        },
        onError: () => {
          if (!basicEventForm.title || basicEventForm.title.trim() === '') {
            alert('이벤트 이름을 입력해 주세요');
          } else if (!basicEventForm.description || basicEventForm.description.trim() === '') {
            alert('이벤트 설명을 입력해 주세요');
          }
        },
      }
    );
  };

  return (
    <Flex>
      <Flex dir="column" gap="40px" padding="60px 0" width="100%">
        <Flex justifyContent="space-between" alignItems="center" padding="40px 0">
          <Text as="h1" type="Display" weight="bold">
            {isEdit ? '이벤트 수정' : '이벤트 생성하기'}
          </Text>
          <Flex gap="8px">
            <Button size="sm" onClick={templateModalOpen}>
              템플릿
            </Button>
          </Flex>
        </Flex>

        <Flex dir="column" gap="30px">
          <Flex dir="column" gap="8px">
            <Flex justifyContent="space-between">
              <label htmlFor="title">
                <Text type="Heading" weight="medium">
                  이벤트 이름
                </Text>
              </label>
              <Flex
                onClick={handleLoadPastEvent}
                css={css`
                  cursor: pointer;
                `}
              >
                <Text type="Label" color="gray">
                  +현재 글 템플릿에 추가
                </Text>
              </Flex>
            </Flex>
            <Input
              id="title"
              name="title"
              placeholder="이벤트 이름을 입력해주세요"
              value={basicEventForm.title}
              onChange={handleChange}
              errorMessage={errors.title}
              isRequired
              showCounter
              maxLength={MAX_LENGTH.TITLE}
            />
          </Flex>

          <Flex
            gap="16px"
            css={css`
              @media (max-width: 768px) {
                flex-direction: column;
                gap: 30px;
              }
            `}
          >
            <Flex
              dir="column"
              gap="8px"
              css={css`
                flex: 1;
              `}
            >
              <label htmlFor="eventStart">
                <Text type="Heading" weight="medium">
                  이벤트 시작일
                </Text>
              </label>
              <Input
                id="eventStart"
                name="eventStart"
                type="datetime-local"
                min="2025-07-31T14:00"
                placeholder="2025.07.30 13:00"
                value={basicEventForm.eventStart}
                onChange={(e) => {
                  handleChange(e);
                  const registrationEndValue = e.target.value;
                  handleValueChange('registrationEnd', registrationEndValue);
                  validateField('registrationEnd', registrationEndValue);
                }}
                errorMessage={errors.eventStart}
                isRequired
              />
            </Flex>

            <Flex
              dir="column"
              gap="8px"
              css={css`
                flex: 1;
              `}
            >
              <label htmlFor="eventEnd">
                <Text type="Heading" weight="medium">
                  이벤트 종료일
                </Text>
              </label>
              <Input
                id="eventEnd"
                name="eventEnd"
                type="datetime-local"
                placeholder="2025.07.30 15:00"
                value={basicEventForm.eventEnd}
                min={basicEventForm.eventStart}
                onChange={handleChange}
                errorMessage={errors.eventEnd}
                isRequired
              />
            </Flex>
          </Flex>

          <Flex
            gap="16px"
            css={css`
              @media (max-width: 768px) {
                flex-direction: column;
                gap: 30px;
              }
            `}
          >
            <Flex
              dir="column"
              gap="8px"
              css={css`
                flex: 1;
              `}
            >
              <label htmlFor="registrationEnd">
                <Text type="Heading" weight="medium">
                  신청 종료일
                </Text>
              </label>
              <Input
                id="registrationEnd"
                name="registrationEnd"
                type="datetime-local"
                placeholder="2025.07.25 15:00"
                value={basicEventForm.registrationEnd}
                max={basicEventForm.eventStart}
                onChange={handleChange}
                errorMessage={errors.registrationEnd}
                isRequired
              />
            </Flex>

            <Flex
              dir="column"
              gap="8px"
              css={css`
                flex: 1;
              `}
            >
              <label htmlFor="place">
                <Text type="Heading" weight="medium">
                  이벤트 장소
                </Text>
              </label>
              <Input
                id="place"
                name="place"
                placeholder="이벤트 장소를 입력해 주세요"
                value={basicEventForm.place}
                onChange={handleChange}
                errorMessage={errors.place}
                showCounter
                maxLength={MAX_LENGTH.PLACE}
              />
            </Flex>
          </Flex>

          <Flex dir="column" gap="8px" margin="10px 0">
            <Button
              type="button"
              onClick={capacityModalOpen}
              aria-label="인원 설정"
              css={css`
                width: 100%;
                display: flex;
                justify-content: flex-start;
                align-items: center;
                gap: 12px;
                padding: 4px 0;
                border: 0;
                background: transparent;
                cursor: pointer;

                &:hover {
                  background: ${theme.colors.gray100};
                }
              `}
            >
              <Text type="Body" weight="medium">
                인원
              </Text>
              <Text as="span" type="Body" color="#4b5563" data-role="value">
                {basicEventForm.maxCapacity === UNLIMITED_CAPACITY
                  ? '제한없음 ✎'
                  : `${basicEventForm.maxCapacity}명 ✎`}
              </Text>
            </Button>

            <MaxCapacityModal
              isOpen={isCapacityModalOpen}
              initialValue={
                basicEventForm.maxCapacity === UNLIMITED_CAPACITY ? 10 : basicEventForm.maxCapacity
              }
              onClose={capacityModalClose}
              onSubmit={(value) => {
                handleValueChange('maxCapacity', value);
                validateField('maxCapacity', value.toString());
              }}
            />
          </Flex>

          <Flex dir="column" gap="8px">
            <label htmlFor="description">
              <Text type="Heading" weight="medium">
                소개글
              </Text>
            </label>
            <Textarea
              id="description"
              name="description"
              placeholder="이벤트에 대한 설명을 입력해 주세요"
              value={basicEventForm.description}
              onChange={handleChange}
              errorMessage={errors.description}
              maxLength={MAX_LENGTH.DESCRIPTION}
            />
          </Flex>

          <QuestionForm
            questions={questions}
            addQuestion={addQuestion}
            deleteQuestion={deleteQuestion}
            updateQuestion={updateQuestion}
            isEditable={!isEdit}
          />

          <Flex justifyContent="flex-end">
            <Button
              type="submit"
              color="primary"
              size="full"
              disabled={!isFormReady}
              onClick={handleSubmit}
              css={css`
                margin-top: 40px;
              `}
            >
              {isEdit ? '이벤트 수정' : '이벤트 생성하기'}
            </Button>
          </Flex>
        </Flex>

        <TemplateModal
          isOpen={isTemplateModalOpen}
          onClose={templateModalClose}
          onTemplateSelected={handleTemplateSelected}
          onEventSelected={handleEventSelected}
        />
      </Flex>
    </Flex>
  );
};
