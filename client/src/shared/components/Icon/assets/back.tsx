import { SVGProps } from 'react';

export const Back = ({ width, height, color, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    width={width ?? 24}
    height={height ?? 24}
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    {...props}
  >
    <path
      d="M12 20L4 12M4 12L12 4M4 12L20 12"
      stroke={color ?? '#1B1B1C'}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);
