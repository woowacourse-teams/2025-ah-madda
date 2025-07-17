import { SVGProps } from 'react';

export const Back = ({ width, height, color, ...props }: SVGProps<SVGSVGElement>) => (
  <svg
    width={width ?? 24}
    height={height ?? 24}
    viewBox="0 0 20 19"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    {...props}
  >
    <path
      d="M19.9414 9.81445H2.38281L10.0684 17.5L9.18945 18.3789L0 9.18945L9.18945 0L10.0684 0.878906L2.38281 8.56445H19.9414V9.81445Z"
      fill={color ?? '#2B2B2B'}
    />
  </svg>
);
