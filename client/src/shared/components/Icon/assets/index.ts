import { Back } from './back';
import { Calendar } from './calendar';
import { Clock } from './clock';
import { Close } from './close';
import { Delete } from './delete';
import { Location } from './location';
import { Plus } from './plus';
import { Users } from './users';

export const Icons = {
  back: Back,
  calendar: Calendar,
  clock: Clock,
  close: Close,
  delete: Delete,
  location: Location,
  plus: Plus,
  users: Users,
};

export type IconName = keyof typeof Icons;
export const iconNames = Object.keys(Icons) as IconName[];
