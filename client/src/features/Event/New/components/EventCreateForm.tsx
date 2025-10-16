import { useEffect, useRef, useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { useUpdateEvent } from '@/api/mutations/useUpdateEvent';
import { getEventDetailAPI } from '@/api/queries/event';
import { organizationQueryOptions } from '@/api/queries/organization';
import type { EventTemplateAPIResponse } from '@/api/types/event';
import type { OrganizationMember } from '@/api/types/organizations';
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
import { formatDate } from '@/shared/utils/dateUtils';

import { EventDetail } from '../../types/Event';
import { MAX_LENGTH, UNLIMITED_CAPACITY } from '../constants/errorMessages';
import { useAddEvent } from '../hooks/useAddEvent';
import { useBasicEventForm } from '../hooks/useBasicEventForm';
import { useDropdownStates } from '../hooks/useDropdownStates';
import { useQuestionForm } from '../hooks/useQuestionForm';
import type { TimeValue } from '../types/time';
import { formatDateForInput, parseInputDate, applyTimeToDate } from '../utils/date';
import { timeValueToDate, timeValueFromDate } from '../utils/time';

import { CoHostSelectModal } from './CoHostSelectModal';
import { DatePickerDropdown } from './DatePickerDropdown';
import { MaxCapacityModal } from './MaxCapacityModal';
import { MyPastEventModal } from './MyPastEventModal';
import { QuestionForm } from './QuestionForm';

type EventCreateFormProps = {
  isEdit: boolean;
  eventId?: number;
};

export const EventCreateForm = ({ isEdit, eventId }: EventCreateFormProps) => {
  const navigate = useNavigate();
  const { organizationId } = useParams();
  const { success, error } = useToast();
  const { mutateAsync: addEvent } = useAddEvent(Number(organizationId));
  const { mutateAsync: updateEvent } = useUpdateEvent();

  const { data: eventDetail } = useQuery({
    queryKey: ['event', 'detail', Number(eventId)],
    queryFn: () => getEventDetailAPI(Number(eventId)),
    enabled: isEdit,
  });
  const { data: myProfile } = useQuery({
    ...organizationQueryOptions.profile(Number(organizationId)),
    enabled: !!organizationId,
  });
  const { data: members } = useQuery({
    ...organizationQueryOptions.members(Number(organizationId)),
    enabled: !!organizationId,
  });
  const { data: organizationGroups } = useQuery({
    ...organizationQueryOptions.group(),
    enabled: !!organizationId && !isEdit,
  });

  const {
    isOpen: isCapacityModalOpen,
    open: capacityModalOpen,
    close: capacityModalClose,
  } = useModal();
  const { isOpen: isCohostModalOpen, open: cohostModalOpen, close: cohostModalClose } = useModal();

  const myId = myProfile?.organizationMemberId;

  const { openDropdown, closeDropdown, isOpen } = useDropdownStates();
  const {
    basicEventForm,
    updateAndValidate,
    handleChange,
    errors,
    isValid: isBasicFormValid,
    loadFormData,
  } = useBasicEventForm(isEdit ? eventDetail : undefined, {
    requireGroupSelection: !isEdit,
  });

  const {
    questions,
    addQuestion,
    deleteQuestion,
    updateQuestion,
    isValid: isQuestionValid,
    loadQuestions,
  } = useQuestionForm();

  const [createEventLocked, setCreateEventLocked] = useState(false);
  const {
    isOpen: isTemplateModalOpen,
    open: templateModalOpen,
    close: templateModalClose,
  } = useModal();
  const isFormReady = isBasicFormValid && isQuestionValid;

  const selectableMembers = (members ?? []).filter((m) => m.organizationMemberId !== myId);

  const selectedNames = (() => {
    const ids = basicEventForm.eventOrganizerIds ?? [];
    const names = selectableMembers
      .filter((m) => ids.includes(m.organizationMemberId))
      .map((m) => m.nickname);

    const selfName =
      members?.find((m) => m.organizationMemberId === myId)?.nickname ??
      myProfile?.nickname ??
      '본인';

    return [selfName, ...names];
  })();

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

  const hydratedFromDetailRef = useRef(false);
  useEffect(() => {
    if (!isEdit) return;
    if (hydratedFromDetailRef.current) return;
    if (!eventDetail) return;

    const hasOtherThanMe = (basicEventForm.eventOrganizerIds ?? []).length > 0;
    if (hasOtherThanMe) return;

    let idsFromDetail: number[] | undefined = (eventDetail as EventDetail).eventOrganizerIds;

    if ((!idsFromDetail || idsFromDetail.length === 0) && members) {
      const organizerNicknames: string[] =
        (eventDetail as { organizerNicknames?: string[] }).organizerNicknames ?? [];

      const byNickname = new Map<string, number>(
        members.map((m) => [m.nickname, m.organizationMemberId])
      );

      const mappedIds: Array<number | undefined> = organizerNicknames.map((nick: string) =>
        byNickname.get(nick)
      );

      idsFromDetail = mappedIds.filter(
        (id: number | undefined): id is number => typeof id === 'number'
      );
    }

    if (Array.isArray(idsFromDetail) && idsFromDetail.length > 0) {
      const othersOnly = myId ? idsFromDetail.filter((id) => id !== myId) : idsFromDetail;
      updateAndValidate({ eventOrganizerIds: othersOnly });
      hydratedFromDetailRef.current = true;
    }
  }, [isEdit, eventDetail, members, myId, basicEventForm.eventOrganizerIds, updateAndValidate]);

  const allGroupIds = (organizationGroups ?? []).map((g: { groupId: number }) => g.groupId);
  const areAllSelected =
    allGroupIds.length > 0 &&
    allGroupIds.every((id) => (basicEventForm.groupIds ?? []).includes(id));

  const buildPayload = () => {
    const base = {
      ...basicEventForm,
      eventOrganizerIds: basicEventForm.eventOrganizerIds ?? [],
      questions,
      eventStart: basicEventForm.eventStart,
      eventEnd: basicEventForm.eventEnd,
      registrationEnd: basicEventForm.registrationEnd,
    };

    if (!isEdit) {
      return { ...base, groupIds: basicEventForm.groupIds ?? [] };
    }
    if ((basicEventForm.groupIds?.length ?? 0) > 0) {
      return { ...base, groupIds: basicEventForm.groupIds };
    }
    return base;
  };

  const autoSaveKey =
    isEdit && eventId ? `event-form:draft:edit:${eventId}` : 'event-form:draft:create';

  const { save, restore, clear } = useAutoSessionSave({
    key: autoSaveKey,
    getData: () => ({ basicEventForm, questions }),
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

  const lastSavedSnapshotRef = useRef<string>('');
  const latestGetterRef = useRef<
    () => { basicEventForm: typeof basicEventForm; questions: typeof questions }
  >(() => ({ basicEventForm, questions }));

  useEffect(() => {
    latestGetterRef.current = () => ({ basicEventForm, questions });
  }, [basicEventForm, questions]);

  useEffect(() => {
    const id = setInterval(() => {
      const current = latestGetterRef.current();
      const snapshot = JSON.stringify(current);

      if (snapshot !== lastSavedSnapshotRef.current) {
        const ok = save();
        if (ok) {
          lastSavedSnapshotRef.current = snapshot;
        }
      }
    }, 5000);

    return () => clearInterval(id);
  }, [save]);

  const submitCreate = async (payload: ReturnType<typeof buildPayload>) => {
    const { eventId } = await addEvent(payload);
    clear();
    trackCreateEvent();
    success('😁 이벤트가 성공적으로 생성되었습니다!');
    navigate(`/${organizationId}/event/${eventId}`);
  };

  const submitUpdate = async (eventId: number, payload: ReturnType<typeof buildPayload>) => {
    await updateEvent({ eventId, payload });
    clear();
    success('😁 이벤트가 성공적으로 수정되었습니다!');
    navigate(`/${organizationId}/event/${eventId}`);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (createEventLocked) return;
    if (!isBasicFormValid || !isQuestionValid) return;

    setCreateEventLocked(true);

    try {
      if (!isBasicFormValid || !isQuestionValid) return;

      const payload = buildPayload();

      if (isEdit && eventId) {
        await submitUpdate(eventId, payload);
      } else {
        await submitCreate(payload);
      }
    } catch (err) {
      handleError(err);
      setCreateEventLocked(false);
    }
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

    const newRegistrationEnd = applyTimeToDate(startDate, finalStartTime);
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

  const toggleGroup = (id: number) => {
    const curr = basicEventForm.groupIds ?? [];
    const has = curr.includes(id);
    const next = has ? curr.filter((g) => g !== id) : [...curr, id];
    updateAndValidate({ groupIds: next });
  };
  const toggleAllGroups = () => {
    if (!organizationGroups || organizationGroups.length === 0) return;
    updateAndValidate({ groupIds: areAllSelected ? [] : allGroupIds });
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
              불러오기
            </Button>
          </Flex>
        </Flex>

        <Flex dir="column" gap="30px">
          <Flex dir="column" gap="8px">
            <Text as="label" htmlFor="title" type="Heading" weight="medium">
              이벤트 이름
              <StyledRequiredMark>*</StyledRequiredMark>
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
                <StyledRequiredMark>*</StyledRequiredMark>
              </Text>
              <Input
                id="eventDateRange"
                name="eventDateRange"
                value={
                  basicEventForm.eventStart && basicEventForm.eventEnd
                    ? formatDate({
                        start: basicEventForm.eventStart,
                        end: basicEventForm.eventEnd,
                        pattern: 'YYYY.MM.DD HH:mm',
                      })
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
                <StyledRequiredMark>*</StyledRequiredMark>
              </Text>
              <Input
                id="registrationEnd"
                name="registrationEnd"
                value={
                  basicEventForm.registrationEnd
                    ? formatDate({
                        start: basicEventForm.registrationEnd,
                        pattern: 'YYYY.MM.DD HH:mm',
                      })
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
                disabledDates={
                  basicEventForm.eventStart
                    ? ([parseInputDate(basicEventForm.eventStart)].filter(Boolean) as Date[])
                    : []
                }
                minTime={parseInputDate(basicEventForm.eventStart) || undefined}
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

          {!isEdit && (
            <Flex
              width="100%"
              gap="8px"
              css={css`
                @media (max-width: 768px) {
                  flex-direction: column;
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
                <Text as="label" type="Heading" weight="medium">
                  알림 보낼 그룹
                  <StyledRequiredMark>*</StyledRequiredMark>
                </Text>

                <Flex
                  width="100%"
                  margin="0 0 30px 0"
                  gap="8px"
                  css={css`
                    flex-wrap: wrap;
                  `}
                >
                  <Segment
                    type="button"
                    onClick={toggleAllGroups}
                    isSelected={areAllSelected}
                    aria-pressed={areAllSelected}
                  >
                    <Text
                      weight={areAllSelected ? 'bold' : 'regular'}
                      color={areAllSelected ? theme.colors.primary500 : theme.colors.gray300}
                    >
                      전체
                    </Text>
                  </Segment>

                  {organizationGroups?.map((group: { groupId: number; name: string }) => {
                    const selected = basicEventForm.groupIds?.includes(group.groupId) ?? false;
                    return (
                      <Segment
                        key={group.groupId}
                        type="button"
                        onClick={() => toggleGroup(group.groupId)}
                        isSelected={selected}
                        aria-pressed={selected}
                      >
                        <Text
                          weight={selected ? 'bold' : 'regular'}
                          color={selected ? theme.colors.primary500 : theme.colors.gray300}
                        >
                          {group.name}
                        </Text>
                      </Segment>
                    );
                  })}
                </Flex>
              </Flex>

              <Flex
                dir="column"
                gap="8px"
                width="100%"
                css={css`
                  flex: 1;
                `}
              >
                <Button
                  type="button"
                  onClick={cohostModalOpen}
                  aria-label="공동 주최자 설정"
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
                  <Text type="Heading" weight="medium">
                    주최자
                  </Text>
                  <Text as="span" type="Body" color="#4b5563" data-role="value">
                    {selectedNames.length > 0
                      ? `${selectedNames.slice(0, 1).join(', ')}${
                          selectedNames.length > 1 ? ` 외 ${selectedNames.length - 1}명` : ''
                        } ✎`
                      : '미선택 ✎'}
                  </Text>
                </Button>

                <CoHostSelectModal
                  isOpen={isCohostModalOpen}
                  members={(selectableMembers as OrganizationMember[]) ?? []}
                  initialSelectedIds={(basicEventForm.eventOrganizerIds ?? []).filter(
                    (id) => id !== myId
                  )}
                  maxSelectable={10}
                  onClose={cohostModalClose}
                  onSubmit={(ids) => {
                    updateAndValidate({ eventOrganizerIds: ids });
                  }}
                />
              </Flex>
            </Flex>
          )}

          <Flex dir="column" gap="8px" margin="10px 0">
            <Button
              type="button"
              size="full"
              onClick={capacityModalOpen}
              aria-label="인원 설정"
              css={css`
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
              <Text type="Heading" weight="medium">
                인원
              </Text>
              <Text as="span" type="Body" color="#4b5563" data-role="value">
                {basicEventForm.maxCapacity === UNLIMITED_CAPACITY
                  ? '제한없음 ✎'
                  : `${basicEventForm.maxCapacity.toLocaleString()}명 ✎`}
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
              disabled={!isFormReady || createEventLocked}
              onClick={handleSubmit}
              css={css`
                margin-top: 40px;
              `}
            >
              {isEdit ? '이벤트 수정' : createEventLocked ? '생성중…' : '이벤트 생성하기'}
            </Button>
          </Flex>
        </Flex>

        <MyPastEventModal
          organizationId={Number(organizationId)}
          isOpen={isTemplateModalOpen}
          onClose={templateModalClose}
          onEventSelected={handleEventSelected}
        />
      </Flex>
    </Flex>
  );
};

const StyledRequiredMark = styled.span`
  margin-left: 8px;
  color: ${theme.colors.red600};
`;

const Segment = styled.button<{ isSelected: boolean }>`
  all: unset;
  flex: 0 0 auto;
  word-break: keep-all;
  border: 1.5px solid
    ${(props) => (props.isSelected ? theme.colors.primary500 : theme.colors.gray300)};
  text-align: center;
  border-radius: 8px;
  cursor: pointer;
  padding: 4px 8px;
  white-space: nowrap;

  &:hover {
    border-color: ${theme.colors.primary500};
  }
`;
