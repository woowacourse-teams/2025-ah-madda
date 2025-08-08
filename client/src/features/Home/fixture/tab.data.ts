import { IconName } from '@/shared/components/Icon/assets';

import Alarm1 from '../../../assets/icon/alarm-1.png';
import Alarm2 from '../../../assets/icon/alarm-2.png';
import Alarm3 from '../../../assets/icon/alarm-3.png';
import Alarm4 from '../../../assets/icon/alarm-4.png';

export const TAB_DATA = {
  GUIDE: [
    {
      text: '크롬에서 알림을 허용해주세요.',
      iconName: 'alarm' as IconName,
      imageUrl: Alarm1,
    },
    {
      text: '주소창 오른쪽 상단의 다운로드 아이콘을 클릭해주세요.',
      iconName: 'alarm' as IconName,
      imageUrl: Alarm2,
    },
  ],
  SETTING: [
    {
      text: '자체적으로 chrome, safari 알람이 켜져 있는지 확인해주세요.',
      iconName: 'alarm' as IconName,
      imageUrl: Alarm3,
    },
    {
      text: '주소창에 chrome://settings/content/notifications를 입력하여 차단된 웹사이트에서 ahmadda.com를 삭제해주세요.',
      iconName: 'alarm' as IconName,
      imageUrl: Alarm4,
    },
  ],
};
