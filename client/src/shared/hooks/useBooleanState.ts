import { useState } from 'react';

export const useBooleanState = (initialState = false) => {
  const [value, setValue] = useState(initialState);

  const setTrue = () => setValue(true);
  const setFalse = () => setValue(false);
  const toggle = () => setValue((prev) => !prev);

  return { value, setTrue, setFalse, toggle };
};
