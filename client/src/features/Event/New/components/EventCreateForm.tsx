import { useEffect } from 'react';

import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { useUpdateEvent } from '@/api/mutations/useUpdateEvent';
import { getEventDetailAPI } from '@/api/queries/event';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';
import { useAutoSessionSave } from '@/shared/hooks/useAutoSessionSave';
import { useModal } from '@/shared/hooks/useModal';
import { trackCreateEvent } from '@/shared/lib/gaEvents';

import { UNLIMITED_CAPACITY } from '../constants/errorMessages';
import { useAddEvent } from '../hooks/useAddEvent';
import { useBasicEventForm } from '../hooks/useBasicEventForm';
import { useQuestionForm } from '../hooks/useQuestionForm';
import { useTemplateLoader } from '../hooks/useTemplateLoader';
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
    loadQuestions,
  } = useQuestionForm();

  const isFormReady = isBasicFormValid && isQuestionValid;

  const { template, selectedEventId, handleSelectEvent } = useTemplateLoader();

  const handleTemplateLoad = () => {
    loadFormData(template ?? {});
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

  const autoSaveKey =
    isEdit && eventId ? `event-form:draft:edit:${eventId}` : 'event-form:draft:create';

  const { restore, clear } = useAutoSessionSave({
    key: autoSaveKey,
    data: { basicEventForm, questions },
  });

  useEffect(() => {
    const draft = restore();
    if (!draft) return;

    if (isEdit && !eventDetail) return;
    if (draft.basicEventForm) loadFormData(draft.basicEventForm);
    if (draft.questions) loadQuestions(draft.questions);
  }, [isEdit, eventDetail, restore, loadFormData, loadQuestions]);

  const submitCreate = (payload: ReturnType<typeof buildPayload>) => {
    addEvent(payload, {
      onSuccess: ({ eventId }) => {
        clear();
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
          clear();
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

  return (
    <Flex>
      <Flex dir="column" gap="20px" padding="60px 0" width="100%">
        <Text type="Title" weight="bold">
          {isEdit ? '이벤트 수정' : '새 이벤트 만들기'}
        </Text>
        <Flex justifyContent="space-between" alignItems="center">
          <Text type="Body" color="gray">
            이벤트 정보를 입력해 주세요
          </Text>
          <Button size="sm" onClick={templateModalOpen}>
            템플릿
          </Button>
        </Flex>

        <Card>
          <Flex justifyContent="space-between">
            <Text type="Heading">기본 질문</Text>
          </Flex>
          <Flex dir="column">
            <Input
              id="title"
              name="title"
              label="이벤트 이름"
              value={basicEventForm.title}
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
                value={basicEventForm.eventStart}
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
                value={basicEventForm.eventEnd}
                min={basicEventForm.eventStart}
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
              value={basicEventForm.registrationEnd}
              max={basicEventForm.eventStart}
              onChange={handleChange}
              errorMessage={errors.registrationEnd}
              isRequired
            />

            <Input
              id="place"
              name="place"
              label="장소"
              placeholder="이벤트 장소를 입력해 주세요"
              value={basicEventForm.place}
              onChange={handleChange}
              errorMessage={errors.place}
            />

            <Input
              id="description"
              name="description"
              label="설명"
              placeholder="이벤트에 대한 설명을 입력해 주세요"
              value={basicEventForm.description}
              onChange={handleChange}
              errorMessage={errors.description}
            />

            <Input
              id="maxCapacity"
              name="maxCapacity"
              label="수용 인원"
              value={
                basicEventForm.maxCapacity === UNLIMITED_CAPACITY
                  ? '무제한'
                  : `${basicEventForm.maxCapacity}명`
              }
              readOnly
              onClick={capacityModalOpen}
              css={css`
                cursor: pointer;
              `}
            />

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
        </Card>

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
          >
            {isEdit ? '이벤트 수정' : '이벤트 만들기'}
          </Button>
        </Flex>
      </Flex>

      <TemplateModal
        isOpen={isTemplateModalOpen}
        onClose={templateModalClose}
        onConfirm={handleTemplateLoad}
        onSelect={handleSelectEvent}
        selectedEventId={selectedEventId}
      />
    </Flex>
  );
};
