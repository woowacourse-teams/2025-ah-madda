// S.TODO : 사용하지 않는 코드 추후 제거 예정
// import { useEffect } from 'react';

// import { css } from '@emotion/react';
// import styled from '@emotion/styled';
// import { useSuspenseQuery } from '@tanstack/react-query';

// import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
// import { organizationQueryOptions } from '@/api/queries/organization';
// import { Button } from '@/shared/components/Button';
// import { Flex } from '@/shared/components/Flex';
// import { Input } from '@/shared/components/Input';
// import { Modal } from '@/shared/components/Modal';
// import { Text } from '@/shared/components/Text';
// import { theme } from '@/shared/styles/theme';

// import { useInviteOrganizationProcess } from '../hooks/useInviteOrganizationProcess';
// import { useSpaceJoinForm } from '../hooks/useSpaceJoinForm';

// export const InviteModal = () => {
//   const { nickname, selectedGroup, handleNicknameChange, handleSelectGroup } = useSpaceJoinForm();
//   const { organizationData, handleJoin, handleClose, inviteCode } = useInviteOrganizationProcess();
//   const { data: organizationGroups } = useSuspenseQuery(organizationQueryOptions.group());

//   const handleGoogleLogin = () => {
//     const authUrl = getGoogleAuthUrl();
//     window.location.href = authUrl;
//   };

//   useEffect(() => {
//     if (inviteCode) {
//       sessionStorage.setItem('inviteCode', inviteCode);
//     }
//     return () => {
//       sessionStorage.removeItem('inviteCode');
//     };
//   }, [inviteCode]);

//   // S.TODO : imgurl 처리
//   return (
//     <Modal
//       isOpen={true}
//       onClose={handleClose}
//       css={css`
//         width: 380px;
//       `}
//       showCloseButton={false}
//     >
//       {isAuthenticated() ? (
//         <>
//           <Flex justifyContent="space-between" alignItems="baseline">
//             <Text type="Heading" weight="bold" color="#333">
//               멤버 정보 설정
//             </Text>
//           </Flex>

//           <Flex dir="column" alignItems="flex-start" gap="14px">
//             <Img src={organizationData?.imageUrl} alt={organizationData?.name} />
//             <Text type="Body" weight="regular" color="#666">
//               <Text as="span" type="Body" weight="bold" color={theme.colors.primary700}>
//                 그룹
//               </Text>
//               을 선택해주세요.
//             </Text>

//             <Flex
//               css={css`
//                 flex-wrap: wrap;
//               `}
//               gap="8px"
//               width="100%"
//               justifyContent="center"
//             >
//               {organizationGroups.map((group) => (
//                 <Segment
//                   key={group.groupId}
//                   type="button"
//                   onClick={() => handleSelectGroup(group.groupId)}
//                   isSelected={selectedGroup === group.groupId}
//                   aria-pressed={selectedGroup === group.groupId}
//                 >
//                   <Text
//                     weight={selectedGroup === group.groupId ? 'bold' : 'regular'}
//                     color={
//                       selectedGroup === group.groupId ? theme.colors.white : theme.colors.primary600
//                     }
//                   >
//                     {group.name}
//                   </Text>
//                 </Segment>
//               ))}
//             </Flex>
//             <Text type="Body" weight="regular" color="#666">
//               <Text as="span" type="Body" weight="bold" color={theme.colors.primary700}>
//                 {organizationData?.name}
//               </Text>
//               에서 사용할 닉네임을 입력해주세요.
//             </Text>
//             <Input
//               autoFocus
//               id="nickname"
//               type="text"
//               min={10}
//               placeholder="닉네임을 입력하세요"
//               value={nickname}
//               onChange={handleNicknameChange}
//             />
//           </Flex>
//           <Flex gap="12px" alignItems="center">
//             <Button variant="outline" size="full" onClick={handleClose}>
//               취소
//             </Button>
//             <Button
//               size="full"
//               disabled={!nickname.trim() || selectedGroup == null}
//               onClick={() => {
//                 if (!selectedGroup) return;
//                 handleJoin();
//               }}
//             >
//               참가하기
//             </Button>
//           </Flex>
//         </>
//       ) : (
//         <Flex dir="column" gap="24px" alignItems="center">
//           <Text type="Heading" weight="medium" color={theme.colors.gray900}>
//             로그인이 필요한 서비스입니다.
//           </Text>
//           <Button size="full" onClick={handleGoogleLogin}>
//             로그인
//           </Button>
//         </Flex>
//       )}
//     </Modal>
//   );
// };

// const Segment = styled.button<{ isSelected: boolean }>`
//   all: unset;
//   flex: 0 0 auto;
//   word-break: keep-all;
//   border: 1.5px solid
//     ${(props) => (props.isSelected ? theme.colors.primary300 : theme.colors.gray500)};
//   text-align: center;
//   border-radius: 8px;
//   cursor: pointer;
//   padding: 4px 8px;
//   white-space: nowrap;

//   background: ${(p) => (p.isSelected ? theme.colors.primary400 : theme.colors.primary50)};
//   border: 1.5px solid ${(p) => (p.isSelected ? theme.colors.primary300 : theme.colors.primary200)};

//   &:hover {
//     background: ${(p) => (p.isSelected ? theme.colors.primary600 : theme.colors.primary100)};
//     border-color: ${(p) => (p.isSelected ? theme.colors.primary600 : theme.colors.primary300)};
//   }
// `;

// const Img = styled.img`
//   width: 100%;
//   max-width: 250px;
//   height: auto;
//   margin: 0 auto;
//   padding: 20px 0;
// `;
