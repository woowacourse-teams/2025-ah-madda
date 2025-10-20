// S.TODO : 추후 삭제
// import { css } from '@emotion/react';

// import { Button } from '@/shared/components/Button';
// import { Flex } from '@/shared/components/Flex';
// import { Modal } from '@/shared/components/Modal';
// import { ModalProps } from '@/shared/components/Modal/Modal';
// import { Text } from '@/shared/components/Text';

// import { copyInviteMessage } from '../utils/copyInviteMessage';

// type InviteCodeProps = {
//   inviteCode: string;
// } & ModalProps;

// export const InviteCodeModal = ({ inviteCode, isOpen, onClose }: InviteCodeProps) => {
//   const handleCopyInviteCode = () => {
//     copyInviteMessage(inviteCode);
//     onClose();
//   };

//   return (
//     <Modal
//       isOpen={isOpen}
//       onClose={onClose}
//       css={css`
//         width: 380px;
//       `}
//     >
//       <Flex dir="column" alignItems="flex-start" gap="16px">
//         <Text type="Heading" weight="semibold">
//           초대 코드가 생성됐어요.
//         </Text>
//         <Text type="Body">초대 코드를 복사해 구성원과 공유하세요!</Text>
//         <Text
//           type="Body"
//           color="primary"
//           css={css`
//             &:hover {
//               cursor: pointer;
//               text-decoration: underline;
//             }
//           `}
//         >
//           {inviteCode}
//         </Text>
//         <Button size="full" onClick={handleCopyInviteCode}>
//           초대코드 복사하기
//         </Button>
//       </Flex>
//     </Modal>
//   );
// };
