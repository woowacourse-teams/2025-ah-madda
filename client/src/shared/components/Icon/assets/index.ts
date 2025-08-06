import { Alarm } from './alarm';
import { Back } from './back';
import { Calendar } from './calendar';
import { Chart } from './chart';
import { Clock } from './clock';
import { Close } from './close';
import { Delete } from './delete';
import { Location } from './location';
import { Logo } from './logo';
import { Plus } from './plus';
import { User } from './user';

export const Icons = {
  back: Back,
  calendar: Calendar,
  chart: Chart,
  clock: Clock,
  close: Close,
  delete: Delete,
  location: Location,
  logo: Logo,
  plus: Plus,
  user: User,
  alarm: Alarm,
};

export type IconName = keyof typeof Icons;
export const iconNames = Object.keys(Icons) as IconName[];
