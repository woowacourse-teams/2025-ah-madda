export const colors = {
  primary50: '#E4F2FF',
  primary100: '#BEDEFF',
  primary200: '#93CAFF',
  primary300: '#68B4FF',
  primary400: '#4AA3FF',
  primary500: '#3993FF',
  primary600: '#3D84FF',
  primary700: '#3D71EA',
  primary800: '#3B3DB7',

  secondary50: '#FFFDE9',
  secondary100: '#FFF9C7',
  secondary200: '#FFF5A3',
  secondary300: '#FEF07F',
  secondary400: '#FCEB63',
  secondary500: '#FAE649',
  secondary600: '#FEDA4A',
  secondary700: '#FBC342',
  secondary800: '#F7AB3A',
  secondary900: '#F1852D',

  gray50: '#F8F9FA',
  gray100: '#F1F2F3',
  gray200: '#E8E9EA',
  gray300: '#D9DADB',
  gray400: '#B5B6B7',
  gray500: '#959697',
  gray600: '#6D6E6F',
  gray700: '#595A5B',
  gray800: '#3B3C3C',
  gray900: '#1B1B1C',

  red50: '#FFEBED',
  red100: '#FFCCCF',
  red200: '#FB9893',
  red300: '#F46F69',
  red400: '#FE4B40',
  red500: '#FF381D',
  red600: '#F52C1F',
  red700: '#E31E1A',
  red800: '#D61111',
  red900: '#C80000',

  white: '#FFFFFF',
  black: '#000000',
};

export type ColorType = typeof colors;
export type IconColor = 'primary' | 'primary500' | 'secondary' | 'gray' | 'red' | 'white';

export const colorMap: Record<IconColor, string> = {
  primary: colors.primary800,
  primary500: colors.primary500,
  secondary: colors.secondary900,
  gray: colors.gray800,
  red: colors.red700,
  white: colors.white,
};
