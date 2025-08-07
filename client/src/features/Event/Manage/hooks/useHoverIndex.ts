import { useState } from 'react';

export const useHoverIndex = () => {
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

  const handleChangeHover = (index: number | null) => {
    setHoveredIndex(index);
  };

  return { hoveredIndex, handleChangeHover };
};
