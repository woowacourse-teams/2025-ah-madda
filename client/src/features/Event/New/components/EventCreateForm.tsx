import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { useUpdateEvent } from '@/api/mutations/useUpdateEvent';
import { getEventDetailAPI } from '@/api/queries/event';
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
import { useDropdownStates } from '../hooks/useDropdownStates';
import { useQuestionForm } from '../hooks/useQuestionForm';
import { useTemplateLoader } from '../hooks/useTemplateLoader';
import { convertDatetimeLocalToKSTISOString } from '../utils/convertDatetimeLocalToKSTISOString';
import {
  formatDateForInput,
  formatDateForDisplay,
  parseInputDate,
  applyTimeToDate,
} from '../utils/dateUtils';

import { DatePickerDropdown } from './DatePickerDropdown';
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

  const handleDateRangeSelect = (
    startDate: Date,
    endDate: Date,
    startTime: Date,
    endTime: Date
  ) => {
    const finalStartTime = startTime;
    const finalEndTime = endTime;
    if (!finalStartTime || !finalEndTime) {
      alert('시간이 선택되지 않았습니다. 시간을 먼저 선택해 주세요.');
      return;
    }

    const newStartDate = applyTimeToDate(startDate, finalStartTime);
    console.log('newStartDate', newStartDate);
    const newEndDate = applyTimeToDate(endDate, finalEndTime);

    handleValueChange('eventStart', formatDateForInput(newStartDate));
    handleValueChange('eventEnd', formatDateForInput(newEndDate));

    const currentRegistrationEndTime =
      parseInputDate(basicEventForm.registrationEnd) || finalStartTime;
    const newRegistrationEnd = applyTimeToDate(startDate, currentRegistrationEndTime);
    const finalRegistrationEnd =
      newRegistrationEnd.getTime() > newStartDate.getTime() ? newStartDate : newRegistrationEnd;
    handleValueChange('registrationEnd', formatDateForInput(finalRegistrationEnd));

    validateField('eventStart', formatDateForInput(newStartDate));
    validateField('eventEnd', formatDateForInput(newEndDate));
    validateField('registrationEnd', formatDateForInput(finalRegistrationEnd));
  };

  const handleRegistrationEndSelect = (date: Date, time: Date) => {
    const finalTime = time;
    if (!finalTime) {
      alert('시간이 선택되지 않았습니다. 시간을 먼저 선택해 주세요.');
      return;
    }

    const newDate = applyTimeToDate(date, finalTime);

    handleValueChange('registrationEnd', formatDateForInput(newDate));
    validateField('registrationEnd', formatDateForInput(newDate));
  };

  return (
    <Flex>
      <Flex dir="column" gap="40px" padding="60px 0" width="100%">
        <Flex justifyContent="space-between" alignItems="center" padding="40px 0">
          <Text as="h1" type="Display" weight="bold">
            {isEdit ? '이벤트 수정' : '이벤트 생성하기'}
          </Text>
          <Button size="sm" onClick={templateModalOpen}>
            템플릿
          </Button>
        </Flex>

        <Flex dir="column" gap="30px">
          <Flex dir="column" gap="8px">
            <Text as="label" type="Heading" weight="medium" htmlFor="title">
              이벤트 이름
            </Text>
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
                css={css`
                  cursor: pointer;
                `}
              />
              <DatePickerDropdown
                mode="range"
                isOpen={isOpen('eventDateRange')}
                onClose={() => closeDropdown()}
                onSelect={handleDateRangeSelect}
                initialStartDate={parseInputDate(basicEventForm.eventStart) || null}
                initialEndDate={parseInputDate(basicEventForm.eventEnd) || null}
                initialStartTime={parseInputDate(basicEventForm.eventStart) || undefined}
                initialEndTime={parseInputDate(basicEventForm.eventEnd) || undefined}
                title="이벤트 날짜 및 시간 선택"
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
                initialTime={parseInputDate(basicEventForm.registrationEnd) || undefined}
                title="신청 종료일 및 시간 선택"
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
                handleValueChange('maxCapacity', value);
                validateField('maxCapacity', value.toString());
              }}
            />
          </Flex>

          <Flex dir="column" gap="8px">
            <Text as="label" type="Heading" weight="medium" htmlFor="description">
              소개글
            </Text>
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
          onConfirm={handleTemplateLoad}
          onSelect={handleSelectEvent}
          selectedEventId={selectedEventId}
        />
      </Flex>
    </Flex>
  );
};
