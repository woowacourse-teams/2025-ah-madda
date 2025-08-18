import { ColorType } from './colors';

declare module '@emotion/react' {
  // eslint-disable-next-line @typescript-eslint/consistent-type-definitions
  export interface Theme {
    colors: ColorType;
  }
}
