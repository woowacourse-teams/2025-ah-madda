import { useEffect, useRef } from 'react';

import { boot, loadScript } from '@channel.io/channel-web-sdk-loader';

export const useChannelTalk = () => {
  const channelTalkDidInit = useRef(false);

  useEffect(() => {
    if (typeof window === 'undefined' || channelTalkDidInit.current) {
      return;
    }

    channelTalkDidInit.current = true;
    loadScript();
    boot({
      pluginKey: process.env.CHANNELTALK_PLUGIN ?? '',
      language: 'ko',
    });
  }, []);
};
