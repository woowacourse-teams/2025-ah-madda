import { useMemo } from 'react';

import { EventDetail } from '@/api/types/event';
import { formatDate } from '@/shared/utils/dateUtils';

import { getEventButtonState } from '../utils/getSubmitButtonState';

const speakifyTime = (s: string) =>
  s
    .replace(/(\d{2}):00\b/g, (_, h) => `${parseInt(h, 10)}시`)
    .replace(/(\d{2}):([0-5]\d)\b/g, (_, h, m) => `${parseInt(h, 10)}시 ${parseInt(m, 10)}분`)
    .replace(/\s+/g, ' ')
    .trim();

const spokenSingle = (iso: string) =>
  speakifyTime(
    formatDate({
      start: iso,
      pattern: 'YYYY년 MM월 DD일 E A HH:mm',
      options: { locale: 'ko', dayOfWeek: 'long', hour12: true },
    })
  );

const spokenRange = (startISO: string, endISO: string) =>
  `이벤트 시간: ${spokenSingle(startISO)}부터 ${spokenSingle(endISO)}까지.`;

const ordinalKo = (n: number) =>
  ['첫', '두', '세', '네', '다섯', '여섯', '일곱', '여덟', '아홉', '열'][n - 1] ?? `${n}번째`;

export function useEventIntroSummaryFocus(params: {
  event?: EventDetail;
  isGuest?: boolean;
}): string {
  const { event, isGuest } = params;

  return useMemo(() => {
    if (!event) return '';

    const title = `이벤트 제목: ${event.title}.`;
    const time = spokenRange(event.eventStart, event.eventEnd);

    const capacity =
      event.maxCapacity > 0
        ? `참여 현황: 총 ${event.maxCapacity}명 중 ${event.currentGuestCount}명.`
        : `참여 현황: 현재 ${event.currentGuestCount}명.`;

    const qParts: string[] = [];
    if (event.questions.length > 0) {
      qParts.push(`사전 질문: 총 ${event.questions.length}개.`);
      event.questions.forEach((q, idx) => {
        const ord = ordinalKo(idx + 1);
        qParts.push(`${ord} 번째 질문: ${q.questionText}.`);
      });
    }

    const hasRequired = event.questions.some((q) => q.isRequired);
    const btnState = getEventButtonState({
      registrationEnd: event.registrationEnd,
      isGuest: isGuest ?? false,
      isRequiredAnswerComplete: !hasRequired,
    });

    const buttonSummary =
      btnState.action === 'cancel'
        ? '신청 완료된 이벤트입니다. 버튼을 선택하면 참여 취소가 가능합니다.'
        : btnState.action === 'participate' && !btnState.disabled
          ? '아직 신청하지 않은 이벤트입니다. 버튼을 선택하면 참여가 가능합니다.'
          : btnState.action === 'participate' && btnState.disabled
            ? '아직 신청하지 않은 이벤트입니다. 사전 질문을 모두 작성하면 신청할 수 있습니다.'
            : '마감된 이벤트입니다.';

    const organizers =
      event.organizerNicknames.length === 0
        ? '주최자 정보 없음'
        : event.organizerNicknames.length <= 3
          ? event.organizerNicknames.join(', ')
          : `${event.organizerNicknames.slice(0, 3).join(', ')} 외 ${
              event.organizerNicknames.length - 3
            }명`;

    return [
      title,
      `장소: ${event.place}.`,
      time,
      event.description ? `이벤트 소개: ${event.description}.` : '',
      `마감 시간: ${spokenSingle(event.registrationEnd)}까지.`,
      `주최자: ${organizers}.`,
      capacity,
      ...qParts,
      buttonSummary,
    ]
      .filter(Boolean)
      .join(' ');
  }, [event, isGuest]);
}
