import { SVGProps } from 'react';

export const Save = ({ width, height, color, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    width={width ?? 24}
    height={height ?? 24}
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    {...props}
  >
    <path
      fillRule="evenodd"
      clipRule="evenodd"
      d="
        M6 2h9l5 5v13a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2Z
        M8 4v5h8V7.5L14.5 4H8Z
        M7 13a1 1 0 0 1 1-1h8a1 1 0 0 1 1 1v5H7v-5Z
      "
      fill={color ?? '#1B1B1C'}
    />
  </svg>
);
