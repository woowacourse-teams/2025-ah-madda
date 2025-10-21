import { useEffect, useMemo, useRef } from 'react';

import { formatDate } from '@/shared/utils/dateUtils';

import { EventDetail } from '../../types/Event';
import { getEventButtonState } from '../utils/getSubmitButtonState';

const speakifyTime = (s: string) =>
  s
    .replace(/(\d{2}):00\b/g, (_, h) => `${parseInt(h, 10)}시`)
    .replace(/(\d{2}):([0-5]\d)\b/g, (_, h, m) => `${parseInt(h, 10)}시 ${parseInt(m, 10)}분`)
    .replace(/\s+/g, ' ')
    .trim();

const spokenRange = (startISO: string, endISO: string) => {
  const start = formatDate({
    start: startISO,
    pattern: 'YYYY년 MM월 DD일 E A HH:mm',
    options: { locale: 'ko', dayOfWeek: 'long', hour12: true },
  });
  const end = formatDate({
    start: endISO,
    pattern: 'YYYY년 MM월 DD일 E A HH:mm',
    options: { locale: 'ko', dayOfWeek: 'long', hour12: true },
  });
  return `${speakifyTime(start)}부터 ${speakifyTime(end)}까지`;
};

const spokenSingle = (iso: string) => {
  const single = formatDate({
    start: iso,
    pattern: 'YYYY년 MM월 DD일 E A HH:mm',
    options: { locale: 'ko', dayOfWeek: 'long', hour12: true },
  });
  return speakifyTime(single);
};

const ordinalKo = (n: number) =>
  ['첫', '두', '세', '네', '다섯', '여섯', '일곱', '여덟', '아홉', '열'][n - 1] ?? `${n}번째`;

export function useEventIntroSummaryFocus(params: {
  event?: EventDetail;
  isGuest?: boolean;
  locationKey: string;
}) {
  const { event, isGuest, locationKey } = params;

  const lastSummaryRef = useRef<string | null>(null);
  const focusedOnceRef = useRef(false);

  const summary = useMemo(() => {
    if (!event) return '';

    const titlePart = `이벤트 제목: ${event.title}.`;
    const timePart = `이벤트 시간: ${spokenRange(event.eventStart, event.eventEnd)}.`;

    const capPart =
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

    return [
      titlePart,
      `장소: ${event.place}.`,
      timePart,
      event.description ? `이벤트 소개: ${event.description}.` : '',
      `마감 시간: ${spokenSingle(event.registrationEnd)}까지.`,
      `주최자: ${
        event.organizerNicknames.length <= 3
          ? event.organizerNicknames.join(', ')
          : `${event.organizerNicknames.slice(0, 3).join(', ')} 외 ${event.organizerNicknames.length - 3}명`
      }.`,
      capPart,
      ...qParts,
      buttonSummary,
    ]
      .filter(Boolean)
      .join(' ');
  }, [event, isGuest]);

  useEffect(() => {
    if (!event || !summary) return;

    const btn = document.getElementById('event-submit-button') as HTMLButtonElement | null;
    if (!btn || btn.disabled) return;

    if (lastSummaryRef.current === summary) return;
    lastSummaryRef.current = summary;

    let intro = document.getElementById('event-intro-desc') as HTMLElement | null;
    if (!intro) {
      intro = document.createElement('div');
      intro.id = 'event-intro-desc';
      intro.style.position = 'absolute';
      intro.style.left = '-9999px';
      intro.style.width = '1px';
      intro.style.height = '1px';
      intro.style.overflow = 'hidden';
      document.body.appendChild(intro);
    }
    intro.textContent = summary;

    const addDescribedBy = (el: HTMLElement, id: string) => {
      const prev = el.getAttribute('aria-describedby');
      const tokens = new Set((prev ?? '').split(/\s+/).filter(Boolean));
      el.setAttribute('aria-describedby', Array.from(tokens.add(id)).join(' '));
    };

    const removeDescribedBy = (el: HTMLElement, id: string) => {
      const prev = el.getAttribute('aria-describedby');
      if (!prev) return;
      const next = prev
        .split(/\s+/)
        .filter((x) => x && x !== id)
        .join(' ');
      if (next) el.setAttribute('aria-describedby', next);
      else el.removeAttribute('aria-describedby');
    };

    if (!focusedOnceRef.current) {
      focusedOnceRef.current = true;
      requestAnimationFrame(() =>
        requestAnimationFrame(() => {
          addDescribedBy(btn, 'event-intro-desc');
          btn.focus();
          const onBlur = () => {
            removeDescribedBy(btn, 'event-intro-desc');
            btn.removeEventListener('blur', onBlur);
          };
          btn.addEventListener('blur', onBlur);
        })
      );
    }
  }, [event, summary, locationKey]);
}
