import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';

type PreQuestionModalProps = {
  isOpen: boolean;
  onClose: () => void;
};

export const PreQuestionModal = ({ isOpen, onClose }: PreQuestionModalProps) => {
  //E.TODO 사전 질문 받아오는 api 개발되면 로직 추가
  const guest = {
    nickname: '홍길동',
    answers: [
      {
        question: '질문이 10글자가 넘어요~~~~~~~~~~~~~',
        answer: '답변이 10글자가 넘어요~~~~~~~~~~~~~',
      },
      {
        question: '질문 굉장히 길어요~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~',
        answer: '답변이 10글자가 넘어요~~~~~~~~~~~~~',
      },
    ],
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} showCloseButton={false}>
      <Flex dir="column" gap="20px" padding="24px">
        <Flex justifyContent="space-between" alignItems="center">
          <Text type="Heading" weight="bold" color="black">
            {guest.nickname}님의 사전 질문
          </Text>
        </Flex>

        <Flex dir="column" gap="16px">
          {guest.answers && guest.answers.length > 0 ? (
            guest.answers.map((answer, index) => (
              <Flex key={index} dir="column" gap="8px">
                <Text type="Body" weight="medium" color="#4A5565">
                  {answer.question}
                </Text>
                <Text type="Body" weight="regular" color="black">
                  {answer.answer}
                </Text>
              </Flex>
            ))
          ) : (
            <Text type="Body" weight="regular" color="#666">
              사전 질문이 없습니다.
            </Text>
          )}
        </Flex>
      </Flex>
    </Modal>
  );
};
