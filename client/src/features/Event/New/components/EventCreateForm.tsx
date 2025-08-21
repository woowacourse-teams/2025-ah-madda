import { useEffect, useRef } from 'react';

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
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useAutoSessionSave } from '@/shared/hooks/useAutoSessionSave';
import { useModal } from '@/shared/hooks/useModal';
import { trackCreateEvent } from '@/shared/lib/gaEvents';
import { theme } from '@/shared/styles/theme';

import { MAX_LENGTH, UNLIMITED_CAPACITY } from '../constants/errorMessages';
import { useAddEvent } from '../hooks/useAddEvent';
import { useBasicEventForm } from '../hooks/useBasicEventForm';
import { useDropdownStates } from '../hooks/useDropdownStates';
import { useQuestionForm } from '../hooks/useQuestionForm';
import type { TimeValue } from '../types/time';
import { convertDatetimeLocalToKSTISOString } from '../utils/convertDatetimeLocalToKSTISOString';
import {
  formatDateForInput,
  formatDateForDisplay,
  parseInputDate,
  applyTimeToDate,
} from '../utils/date';
import { timeValueToDate, timeValueFromDate } from '../utils/time';

import { DatePickerDropdown } from './DatePickerDropdown';
import { MaxCapacityModal } from './MaxCapacityModal';
import { MyPastEventModal } from './MyPastEventModal';
import { QuestionForm } from './QuestionForm';
import { TemplateDropdown } from './TemplateDropdown';

const ORGANIZATION_ID = 1; // 임시

type EventCreateFormProps = {
  isEdit: boolean;
  eventId?: number;
};

export const EventCreateForm = ({ isEdit, eventId }: EventCreateFormProps) => {
  const navigate = useNavigate();
  const { success, error } = useToast();
  const { mutate: addEvent } = useAddEvent(ORGANIZATION_ID);
  const { mutate: updateEvent } = useUpdateEvent();
  const { mutate: addTemplate } = useAddTemplate();

  const { data: eventDetail } = useQuery({
    queryKey: ['event', 'detail', Number(eventId)],
    queryFn: () => getEventDetailAPI(Number(eventId)),
    enabled: isEdit,
  });
  const { openDropdown, closeDropdown, isOpen } = useDropdownStates();
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
    updateAndValidate,
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

  const handleTemplateSelected = (
    templateDetail: Pick<TemplateDetailAPIResponse, 'description'>
  ) => {
    loadFormData({
      title: basicEventForm.title,
      description: templateDetail.description,
      place: basicEventForm.place || '',
      maxCapacity: basicEventForm.maxCapacity || UNLIMITED_CAPACITY,
    });
  };

  const handleEventSelected = (eventData: Omit<EventTemplateAPIResponse, 'eventId'>) => {
    loadFormData({
      title: eventData.title,
      description: eventData.description,
      place: eventData.place || '',
      maxCapacity: eventData.maxCapacity || UNLIMITED_CAPACITY,
    });
  };

  const handleError = (err: unknown) => {
    if (err instanceof HttpError) {
      error(err.data?.detail || '일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      return;
    }
    error('네트워크 연결을 확인해주세요.');
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

  const restoredOnceRef = useRef(false);

  useEffect(() => {
    if (restoredOnceRef.current) return;

    const draft = restore();
    if (!draft) return;

    if (isEdit && !eventDetail) return;
    if (draft.basicEventForm) loadFormData(draft.basicEventForm);
    if (draft.questions) loadQuestions(draft.questions);

    restoredOnceRef.current = true;
  }, [isEdit, eventDetail, restore, loadFormData, loadQuestions]);

  const submitCreate = (payload: ReturnType<typeof buildPayload>) => {
    addEvent(payload, {
      onSuccess: ({ eventId }) => {
        clear();
        trackCreateEvent();
        success('😁 이벤트가 성공적으로 생성되었습니다!');
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
          success('😁 이벤트가 성공적으로 수정되었습니다!');
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

  const handleAddTemplate = () => {
    const title = basicEventForm.description.split('\n')[0].trim();

    addTemplate(
      {
        title: title,
        description: basicEventForm.description,
      },
      {
        onSuccess: () => {
          success('템플릿이 성공적으로 추가되었습니다!');
        },
        onError: () => {
          if (!basicEventForm.description || basicEventForm.description.trim() === '') {
            error('이벤트 설명을 입력해 주세요');
          }
        },
      }
    );
  };

  const handleDateRangeSelect = (
    startDate: Date,
    endDate: Date,
    startTime: TimeValue,
    endTime: TimeValue
  ) => {
    if (!startTime || !endTime) {
      error('시간이 선택되지 않았습니다. 시간을 먼저 선택해 주세요.');
      return;
    }

    const finalStartTime = timeValueToDate(startTime, startDate);
    const finalEndTime = timeValueToDate(endTime, endDate);

    if (!finalStartTime || !finalEndTime) {
      error('시간 처리 중 오류가 발생했습니다.');
      return;
    }

    const currentRegistrationEndTime =
      parseInputDate(basicEventForm.registrationEnd) || finalStartTime;
    const newRegistrationEnd = applyTimeToDate(startDate, currentRegistrationEndTime);
    const finalRegistrationEnd =
      newRegistrationEnd.getTime() > finalStartTime.getTime() ? finalStartTime : newRegistrationEnd;

    updateAndValidate({
      eventStart: formatDateForInput(finalStartTime),
      eventEnd: formatDateForInput(finalEndTime),
      registrationEnd: formatDateForInput(finalRegistrationEnd),
    });
  };

  const handleRegistrationEndSelect = (date: Date, time: TimeValue) => {
    if (!time) {
      error('시간이 선택되지 않았습니다. 시간을 먼저 선택해 주세요.');
      return;
    }

    const finalTime = timeValueToDate(time, date);
    if (!finalTime) {
      error('시간 처리 중 오류가 발생했습니다.');
      return;
    }

    updateAndValidate({ registrationEnd: formatDateForInput(finalTime) });
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
              나의 이벤트
            </Button>
          </Flex>
        </Flex>

        <Flex dir="column" gap="30px">
          <Flex dir="column" gap="8px">
            <Flex justifyContent="space-between">
              <Text as="label" htmlFor="title" type="Heading" weight="medium">
                이벤트 이름
              </Text>
              <Flex
                onClick={handleAddTemplate}
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
                position: relative;
              `}
            >
              <Text as="label" type="Heading" weight="medium" htmlFor="eventDateRange">
                이벤트 기간
              </Text>
              <Input
                id="eventDateRange"
                name="eventDateRange"
                value={
                  basicEventForm.eventStart && basicEventForm.eventEnd
                    ? `${formatDateForDisplay(basicEventForm.eventStart)} ~ ${formatDateForDisplay(basicEventForm.eventEnd)}`
                    : ''
                }
                placeholder="이벤트 시작일과 종료일을 선택해주세요"
                readOnly
                onClick={() => openDropdown('eventDateRange')}
                errorMessage={errors.eventStart || errors.eventEnd}
                isRequired
                onClear={() => {
                  updateAndValidate({ eventStart: '', eventEnd: '' });
                }}
                css={css`
                  cursor: pointer;
                `}
              />
              <DatePickerDropdown
                mode="range"
                isOpen={isOpen('eventDateRange')}
                onClose={() => closeDropdown()}
                onSelect={handleDateRangeSelect}
                initialStartDate={parseInputDate(basicEventForm.eventStart)}
                initialEndDate={parseInputDate(basicEventForm.eventEnd)}
                initialStartTime={timeValueFromDate(parseInputDate(basicEventForm.eventStart))}
                initialEndTime={timeValueFromDate(parseInputDate(basicEventForm.eventEnd))}
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
                position: relative;
              `}
            >
              <Text as="label" type="Heading" weight="medium" htmlFor="registrationEnd">
                신청 종료일
              </Text>
              <Input
                id="registrationEnd"
                name="registrationEnd"
                value={
                  basicEventForm.registrationEnd
                    ? formatDateForDisplay(basicEventForm.registrationEnd)
                    : ''
                }
                placeholder="신청 종료일과 시간을 선택해주세요"
                readOnly
                onClick={() => openDropdown('registrationEnd')}
                errorMessage={errors.registrationEnd}
                isRequired
                onClear={() => {
                  updateAndValidate({ registrationEnd: '' });
                }}
                css={css`
                  cursor: pointer;
                `}
              />
              <DatePickerDropdown
                mode="single"
                isOpen={isOpen('registrationEnd')}
                onClose={() => closeDropdown()}
                onSelect={handleRegistrationEndSelect}
                initialDate={parseInputDate(basicEventForm.registrationEnd) || null}
                initialTime={timeValueFromDate(parseInputDate(basicEventForm.registrationEnd))}
              />
            </Flex>

            <Flex
              dir="column"
              gap="8px"
              css={css`
                flex: 1;
              `}
            >
              <Text as="label" type="Heading" weight="medium" htmlFor="place">
                이벤트 장소
              </Text>
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
                updateAndValidate({ maxCapacity: value });
              }}
            />
          </Flex>

          <Flex dir="column" gap="8px">
            <Flex
              justifyContent="space-between"
              alignItems="flex-start"
              css={css`
                @media (max-width: 768px) {
                  flex-direction: column;
                  gap: 12px;
                }
              `}
            >
              <Flex
                dir="row"
                justifyContent="space-between"
                alignItems="center"
                width="100%"
                gap="8px"
              >
                <Text as="label" htmlFor="description" type="Heading" weight="medium">
                  소개글
                </Text>
                <Flex
                  css={css`
                    width: 320px;
                    @media (max-width: 768px) {
                      width: 100%;
                    }
                  `}
                >
                  <TemplateDropdown onTemplateSelected={handleTemplateSelected} />
                </Flex>
              </Flex>
            </Flex>
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

        <MyPastEventModal
          isOpen={isTemplateModalOpen}
          onClose={templateModalClose}
          onEventSelected={handleEventSelected}
        />
      </Flex>
    </Flex>
  );
};
