import { SVGProps } from 'react';

export const Pencil = ({ width, height, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    width={width ?? 24}
    height={height ?? 24}
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
    {...props}
  >
    <g
      transform="rotate(-45 12 12)"
      stroke="currentColor"
      strokeWidth={2}
      strokeLinecap="round"
      strokeLinejoin="round"
      vectorEffect="non-scaling-stroke"
    >
      <rect x="8.5" y="3" width="7" height="12.5" rx="3.5" />
      <path d="M8.5 6.25h7" />
      <path d="M8.5 15.5L12 20l3.5-4.5" />
    </g>
  </svg>
);
