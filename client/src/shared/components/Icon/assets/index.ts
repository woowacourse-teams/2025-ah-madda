import { Alarm } from './alarm';
import { Back } from './back';
import { Calendar } from './calendar';
import { Chart } from './chart';
import { Clock } from './clock';
import { Close } from './close';
import { Delete } from './delete';
import { DropdownDown } from './dropdownDown';
import { DropdownUp } from './dropdownUp';
import { Edit } from './edit';
import { Location } from './location';
import { Logo } from './logo';
import { Next } from './next';
import { Plus } from './plus';
import { Save } from './save';
import { Setting } from './setting';
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
  edit: Edit,
  location: Location,
  logo: Logo,
  plus: Plus,
  share: Share,
  user: User,
  dropdownDown: DropdownDown,
  dropdownUp: DropdownUp,
  next: Next,
  save: Save,
  setting: Setting,
};

export type IconName = keyof typeof Icons;
export const iconNames = Object.keys(Icons) as IconName[];
