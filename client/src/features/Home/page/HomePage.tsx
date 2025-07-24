import { Button } from '@/shared/components/Button';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';

import { Description } from '../component/Description';
import { Info } from '../component/Info';

export const HomePage = () => {
  return (
    <PageLayout
      header={
        <Header
          left={<Icon name="logo" width={55} />}
          right={
            <Button width="80px" size="sm">
              로그인
            </Button>
          }
        />
      }
    >
      <Info />
      <Description />
    </PageLayout>
  );
};
