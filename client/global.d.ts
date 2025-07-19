/// <reference types="@emotion/react/types/css-prop" />

declare module '*.svg' {
  import { ComponentType, SVGProps } from 'react';
  const SVGComponent: ComponentType<SVGProps<SVGSVGElement>>;
  export default SVGComponent;
}
