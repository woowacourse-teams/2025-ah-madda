import styled from '@emotion/styled';

import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

type Template = {
  templateId: number;
  title: string;
};

type TemplateListProps = {
  templates: Template[];
  selectedId: number;
  onSelectTemplate: (templateId: number) => void;
};

type StyledCardProps = {
  isSelected: boolean;
};

export const TemplateList = ({ templates, selectedId, onSelectTemplate }: TemplateListProps) => {
  return (
    <Flex dir="column" gap="16px" padding="20px 0">
      <Text type="Body" weight="regular" color={theme.colors.gray600}>
        저장된 템플릿이 여기에 표시됩니다.
      </Text>
      {templates?.map((template) => {
        const isSelected = selectedId === template.templateId;

        return (
          <StyledCard
            key={template.templateId}
            isSelected={isSelected}
            onClick={() => onSelectTemplate(template.templateId)}
          >
            <Text type="Body" weight="medium" color={theme.colors.gray900}>
              {template.title}
            </Text>
          </StyledCard>
        );
      })}

      {templates?.length === 0 && (
        <Flex alignItems="center" justifyContent="center" padding="40px 0">
          <Text type="Body" weight="regular" color={theme.colors.gray500}>
            아직 저장된 템플릿이 없습니다.
          </Text>
        </Flex>
      )}
    </Flex>
  );
};

const StyledCard = styled(Card)<StyledCardProps>`
  cursor: pointer;
  padding: 16px;
  background-color: ${({ isSelected }) =>
    isSelected ? theme.colors.primary50 : theme.colors.white};
  border-radius: 8px;
  transition: all 0.2s ease;
  &:hover {
    background-color: ${theme.colors.primary50};
  }
`;
