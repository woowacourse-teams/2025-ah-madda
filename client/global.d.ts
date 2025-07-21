/// <reference types="@emotion/react/types/css-prop" />

declare module '*.png';
declare module '*.jpg';
declare module '*.jpeg';
declare module '*.gif';

declare module '*.svg' {
  import { ComponentType, SVGProps } from 'react';
  const SVGComponent: ComponentType<SVGProps<SVGSVGElement>>;
  export default SVGComponent;
}
