import { useNavigate, useParams } from 'react-router-dom';

import { HttpError } from '@/api/fetcher';
import { useCloseEventRegistration } from '@/api/mutations/useCloseEventRegistration';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';

import { DeadlineModal } from './info/DeadlineModal';

export const EventManageFooter = ({
  eventId,
  registrationEnd,
}: {
  eventId: number;
  registrationEnd: string;
}) => {
  const { success, error } = useToast();
  const navigate = useNavigate();
  const { organizationId } = useParams();
  const isClosed = registrationEnd ? new Date(registrationEnd) < new Date() : false;
  const { isOpen, open, close } = useModal();

  const { mutate: closeEventRegistrationMutate } = useCloseEventRegistration(eventId);

  const handleDeadlineChangeClick = () => {
    closeEventRegistrationMutate(undefined, {
      onSuccess: () => {
        success('이벤트가 마감되었습니다.');
        close();
      },
      onError: (err) => {
        if (err instanceof HttpError) {
          error(err.message);
        }
      },
    });
  };

  return (
    <>
      <Flex justifyContent="flex-end" margin="0 30px 24px 0">
        {isClosed ? (
          <Button size="sm" color="tertiary" variant="solid" disabled>
            마감됨
          </Button>
        ) : (
          <Flex alignItems="center" gap="8px">
            <Button
              size="md"
              color="primary"
              variant="solid"
              onClick={() => navigate(`/${organizationId}/event/${eventId}/edit`)}
            >
              수정하기
            </Button>
            <Button size="md" color="secondary" variant="solid" onClick={open}>
              마감하기
            </Button>
          </Flex>
        )}
      </Flex>
      <DeadlineModal isOpen={isOpen} onClose={close} onDeadlineChange={handleDeadlineChangeClick} />
    </>
  );
};
