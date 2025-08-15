import { css } from '@emotion/react';

import { useDeleteTemplate } from '@/api/mutations/useDeleteTemplate';
import { Flex } from '@/shared/components/Flex';
import { IconButton } from '@/shared/components/IconButton';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { SelectableCard } from './SelectableCard';

type Template = {
  templateId: number;
  title: string;
};

type TemplateListProps = {
  templates: Template[];
  selectedId: number | null;
  onSelectTemplate: (templateId: number | null) => void;
};

export const TemplateList = ({ templates, selectedId, onSelectTemplate }: TemplateListProps) => {
  const { mutate: deleteTemplate } = useDeleteTemplate();

  const handleDeleteTemplate = (e: React.MouseEvent, templateId: number) => {
    e.stopPropagation();
    if (!confirm('정말 이 템플릿을 삭제하시겠어요?')) return;
    deleteTemplate(templateId, {
      onSuccess: () => {
        if (selectedId === templateId) {
          onSelectTemplate(null);
        }
        alert('템플릿이 삭제되었습니다.');
      },
      onError: (error) => {
        console.error(error);
        alert('템플릿 삭제에 실패했습니다. 잠시 후 다시 시도해 주세요.');
      },
    });
  };

  return (
    <Flex dir="column" gap="16px" padding="20px 0">
      <Text type="Body" weight="regular" color={theme.colors.gray600}>
        저장된 템플릿이 여기에 표시됩니다.
      </Text>

      <Flex
        dir="column"
        gap="12px"
        css={css`
          max-height: 200px;
          overflow-y: auto;

          &::-webkit-scrollbar {
            width: 6px;
          }

          &::-webkit-scrollbar-thumb {
            background: ${theme.colors.gray300};
            border-radius: 3px;

            &:hover {
              background: ${theme.colors.gray400};
            }
          }
        `}
      >
        {templates?.map((template) => {
          const isSelected = selectedId !== null && selectedId === template.templateId;

          return (
            <SelectableCard
              key={template.templateId}
              isSelected={isSelected}
              onClick={() => onSelectTemplate(template.templateId)}
            >
              <Flex justifyContent="space-between" alignItems="center">
                <Text type="Body" weight="medium" color={theme.colors.gray900}>
                  {template.title.length > 21
                    ? `${template.title.slice(0, 21)}...`
                    : template.title}
                </Text>
                <IconButton
                  name="delete"
                  size={20}
                  color="gray"
                  onClick={(e) => handleDeleteTemplate(e, template.templateId)}
                />
              </Flex>
            </SelectableCard>
          );
        })}
      </Flex>

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
