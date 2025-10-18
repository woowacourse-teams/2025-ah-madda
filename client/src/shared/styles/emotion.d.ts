import { ColorType } from './colors';
import { SpacingType } from './spacing';

declare module '@emotion/react' {
  // eslint-disable-next-line @typescript-eslint/consistent-type-definitions
  export interface Theme {
    colors: ColorType;
    spacing: SpacingType;
  }
}
