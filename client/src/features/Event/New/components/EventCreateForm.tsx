import { css } from '@emotion/react';

import { Button } from '../../../../shared/components/Button';
import { Card } from '../../../../shared/components/Card';
import { Flex } from '../../../../shared/components/Flex';
import { Input } from '../../../../shared/components/Input';
import { Text } from '../../../../shared/components/Text';

export const EventCreateForm = () => {
  return (
    <Flex dir="column" gap="20px" padding="40px 0" width="100%">
      <Text type="Title" weight="bold">
        새 이벤트 만들기
      </Text>
      <Text type="caption" color="gray">
        이벤트 정보를 입력해 주세요
      </Text>

      <Card>
        <Text type="caption">기본 질문</Text>
        <Flex dir="column" gap="8px">
          <Flex dir="column">
            <Input id="title" label="이벤트 이름" placeholder="이벤트 이름을 입력해 주세요" />
          </Flex>

          <Flex gap="16px">
            <Flex
              dir="column"
              css={css`
                flex: 1;
              `}
            >
              <Input id="eventStart" label="이벤트 시작 날짜/시간" placeholder="2025.07.30 13:00" />
            </Flex>
            <Flex
              dir="column"
              css={css`
                flex: 1;
              `}
            >
              <Input id="eventEnd" label="이벤트 종료 날짜/시간" placeholder="2025.07.30 15:00" />
            </Flex>
          </Flex>

          <Flex gap="16px">
            <Flex
              dir="column"
              css={css`
                flex: 1;
              `}
            >
              <Input
                id="registrationStart"
                label="신청 시작 날짜/시간"
                placeholder="2025.07.25 13:00"
              />
            </Flex>
            <Flex
              dir="column"
              css={css`
                flex: 1;
              `}
            >
              <Input
                id="registrationEnd"
                label="신청 종료 날짜/시간"
                placeholder="2025.07.25 15:00"
              />
            </Flex>
          </Flex>

          <Flex dir="column">
            <Input id="place" label="장소" placeholder="이벤트 장소를 입력해 주세요" />
          </Flex>

          <Flex dir="column">
            <Input id="description" label="설명" placeholder="이벤트에 대한 설명을 입력해 주세요" />
          </Flex>

          <Flex gap="16px">
            <Flex
              dir="column"
              css={css`
                flex: 1;
              `}
            >
              <Input id="author" label="주최자 이름" placeholder="주최자 이름을 입력해 주세요" />
            </Flex>
            <Flex
              css={css`
                flex: 1;
              `}
            >
              <Input
                id="maxCapacity"
                label="수용 인원"
                placeholder="최대 참가 인원을 입력해 주세요"
              />
            </Flex>
          </Flex>
        </Flex>
      </Card>

      <Flex justifyContent="flex-end">
        <Button
          type="submit"
          color="black"
          size="sm"
          css={css`
            border-radius: 5px;
            font-size: 12px;
            padding: 7px;
          `}
        >
          이벤트 만들기
        </Button>
      </Flex>
    </Flex>
  );
};
