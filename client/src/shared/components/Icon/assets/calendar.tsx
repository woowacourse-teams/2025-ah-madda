import { SVGProps } from 'react';

export const Calendar = ({ width, height, color, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    width={width ?? 24}
    height={height ?? 24}
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    {...props}
  >
    <path
      d="M4 10.8267H20M16.4444 4V7.41333M7.55556 4V7.41333M16.4444 5.70667H7.55556C6.61256 5.70667 5.70819 6.06628 5.0414 6.70641C4.3746 7.34653 4 8.21473 4 9.12V16.5867C4 17.4919 4.3746 18.3601 5.0414 19.0003C5.70819 19.6404 6.61256 20 7.55556 20H16.4444C17.3874 20 18.2918 19.6404 18.9586 19.0003C19.6254 18.3601 20 17.4919 20 16.5867V9.12C20 8.21473 19.6254 7.34653 18.9586 6.70641C18.2918 6.06628 17.3874 5.70667 16.4444 5.70667Z"
      stroke={color ?? '#2B2B2B'}
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);
