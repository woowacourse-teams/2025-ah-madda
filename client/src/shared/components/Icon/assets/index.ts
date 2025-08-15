import { Alarm } from './alarm';
import { Back } from './back';
import { Calendar } from './calendar';
import { Chart } from './chart';
import { Clock } from './clock';
import { Close } from './close';
import { Delete } from './delete';
import { DropdownDown } from './dropdownDown';
import { DropdownUp } from './dropdownUp';
import { Location } from './location';
import { Logo } from './logo';
import { Plus } from './plus';
import { Share } from './share';
import { User } from './user';

export const Icons = {
  alarm: Alarm,
  back: Back,
  calendar: Calendar,
  chart: Chart,
  clock: Clock,
  close: Close,
  delete: Delete,
  location: Location,
  logo: Logo,
  plus: Plus,
  share: Share,
  user: User,
  dropdownDown: DropdownDown,
  dropdownUp: DropdownUp,
};

export type IconName = keyof typeof Icons;
export const iconNames = Object.keys(Icons) as IconName[];
