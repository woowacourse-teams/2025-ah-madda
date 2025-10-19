import LANDING_IMAGE_1 from '@/assets/icon/landing-1.webp';
import LANDING_IMAGE_2 from '@/assets/icon/landing-2.webp';
import LANDING_IMAGE_3 from '@/assets/icon/landing-3.webp';
import LANDING_IMAGE_4 from '@/assets/icon/landing-4.webp';

export const LANDING = [
  {
    message: '채널이 너무 많아 정보가 분산되어 알 수 없었다.',
    image: LANDING_IMAGE_1,
  },
  {
    message: '나중에 신청하려고 했으나, 깜빡해서 참여 기회를 놓쳤다.',
    image: LANDING_IMAGE_2,
  },
  {
    message: '메세지 읽음 여부를 확인이 불가하다.',
    image: LANDING_IMAGE_3,
  },
  {
    message: '리마인더 메시지를 수동으로 다시 보내야 한다.',
    image: LANDING_IMAGE_4,
  },
] as const;
