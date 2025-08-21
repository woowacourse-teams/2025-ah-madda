import { useState, useEffect } from 'react';

import { useQuery, useSuspenseQueries } from '@tanstack/react-query';

import { useDeleteTemplate } from '@/api/mutations/useDeleteTemplate';
import { eventQueryOptions } from '@/api/queries/event';
import type { TemplateDetailAPIResponse } from '@/api/types/event';
import { Dropdown } from '@/shared/components/Dropdown';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';
import { trackLoadTemplate } from '@/shared/lib/gaEvents';
import { theme } from '@/shared/styles/theme';
import { truncateText } from '@/shared/utils/text';

import { TemplateDeleteModal } from './TemplateDeleteModal';

type TemplateDropdownProps = {
  onTemplateSelected: (templateDetail: Pick<TemplateDetailAPIResponse, 'description'>) => void;
};

export const TemplateDropdown = ({ onTemplateSelected }: TemplateDropdownProps) => {
  const [selectedTemplate, setSelectedTemplate] = useState('템플릿을 선택하세요');
  const [selectedTemplateId, setSelectedTemplateId] = useState<number | null>(null);
  const [deleteTargetId, setDeleteTargetId] = useState<number | null>(null);

  const [{ data: templateList }] = useSuspenseQueries({
    queries: [eventQueryOptions.templateList()],
  });

  const { mutate: deleteTemplate } = useDeleteTemplate();

  const { isOpen, open, close } = useModal();

  const { data: selectedTemplateData } = useQuery({
    ...eventQueryOptions.templateDetail(selectedTemplateId!),
    enabled: selectedTemplateId !== null,
  });
  const { error, success } = useToast();

  useEffect(() => {
    if (selectedTemplateData && selectedTemplateId) {
      trackLoadTemplate(selectedTemplateId);

      onTemplateSelected({ description: selectedTemplateData.description });

      setSelectedTemplateId(null);
    }
  }, [selectedTemplateData, selectedTemplateId, onTemplateSelected]);

  const handleTemplateSelect = (templateId: number) => {
    setSelectedTemplate(
      templateList.find((template) => template.templateId === templateId)?.title || ''
    );
    setSelectedTemplateId(templateId);
  };

  const handleDeleteClick = (templateId: number) => {
    const template = templateList.find((t) => t.templateId === templateId);
    if (template) {
      setDeleteTargetId(templateId);
      open();
    }
  };

  const handleDeleteConfirm = () => {
    if (deleteTargetId) {
      deleteTemplate(deleteTargetId, {
        onSuccess: () => {
          success('템플릿이 성공적으로 삭제되었습니다!');
          close();
          setDeleteTargetId(null);
        },
        onError: () => {
          error('템플릿 삭제에 실패했습니다.');
        },
      });
    }
  };

  const handleDeleteCancel = () => {
    close();
    setDeleteTargetId(null);
  };

  return (
    <Flex dir="column" gap="8px" width="100%">
      <Dropdown>
        <Dropdown.Trigger>
          <Flex justifyContent="space-between" alignItems="center" width="100%" padding="8px">
            <Text type="Body" color={theme.colors.gray700}>
              {truncateText(selectedTemplate)}
            </Text>
            <Icon name="dropdownDown" size={16} color="gray500" />
          </Flex>
        </Dropdown.Trigger>

        <Dropdown.Content>
          {templateList && templateList.length > 0 ? (
            templateList.map((template) => (
              <Dropdown.Item
                key={template.templateId}
                onClick={() => handleTemplateSelect(template.templateId)}
              >
                <Flex justifyContent="space-between" alignItems="center" width="100%">
                  <Text type="Body" color={theme.colors.gray800}>
                    {truncateText(template.title)}
                  </Text>
                  <Flex
                    alignItems="center"
                    justifyContent="center"
                    width="24px"
                    height="24px"
                    gap="4px"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDeleteClick(template.templateId);
                    }}
                  >
                    <Icon name="delete" size={16} color="gray500" />
                  </Flex>
                </Flex>
              </Dropdown.Item>
            ))
          ) : (
            <Flex padding="20px" justifyContent="center">
              <Text type="Body" color={theme.colors.gray500}>
                저장된 템플릿이 없습니다
              </Text>
            </Flex>
          )}
        </Dropdown.Content>
      </Dropdown>

      <TemplateDeleteModal
        isOpen={isOpen}
        onClose={handleDeleteCancel}
        onDeleteConfirm={handleDeleteConfirm}
      />
    </Flex>
  );
};
