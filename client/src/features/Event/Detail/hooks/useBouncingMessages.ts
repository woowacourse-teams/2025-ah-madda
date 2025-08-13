import { useState } from 'react';

type BouncingMessage = {
  id: number;
  message: string;
  x: number;
  y: number;
  moveX: number;
  moveY: number;
};

const POKE_MESSAGES = ['👆 콕!', '쿡ㅋ', '콕 찌르기', '콕콕콕', '👆👆👆'] as const;
const DISTANCE = 80;
const RANDOM_DIRECTION = Math.random() * 360;
const MESSAGE_ID = Date.now();

export const useBouncingMessages = () => {
  const [bouncingMessages, setBouncingMessages] = useState<BouncingMessage[]>([]);
  const randomMessage = POKE_MESSAGES[Math.floor(Math.random() * POKE_MESSAGES.length)];

  const addBouncingMessage = (event: React.MouseEvent) => {
    const rect = (event.target as HTMLElement).getBoundingClientRect();
    const randomStartX = rect.left + Math.random() * rect.width;

    const moveX = Math.cos((RANDOM_DIRECTION * Math.PI) / 180) * DISTANCE;
    const moveY = Math.sin((RANDOM_DIRECTION * Math.PI) / 180) * DISTANCE;

    setBouncingMessages((prev) => [
      ...prev,
      {
        id: MESSAGE_ID,
        message: randomMessage,
        x: randomStartX,
        y: rect.top + rect.height / 2,
        moveX,
        moveY,
      },
    ]);

    setTimeout(() => {
      setBouncingMessages((prev) => prev.filter((msg) => msg.id !== MESSAGE_ID));
    }, 2000);
  };

  return { bouncingMessages, addBouncingMessage };
};
