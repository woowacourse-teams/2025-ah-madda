import { useState, useEffect } from 'react';

import { useQuery, useSuspenseQueries } from '@tanstack/react-query';

import { eventQueryOptions } from '@/api/queries/event';
import type { TemplateDetailAPIResponse } from '@/api/types/event';
import { Dropdown } from '@/shared/components/Dropdown';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';
import { trackLoadTemplate } from '@/shared/lib/gaEvents';
import { theme } from '@/shared/styles/theme';

type TemplateDropdownProps = {
  onTemplateSelected: (templateDetail: Pick<TemplateDetailAPIResponse, 'description'>) => void;
};

export const TemplateDropdown = ({ onTemplateSelected }: TemplateDropdownProps) => {
  const [selectedTemplate, setSelectedTemplate] = useState('템플릿을 선택하세요');
  const [selectedTemplateId, setSelectedTemplateId] = useState<number | null>(null);
  const [{ data: templateList }] = useSuspenseQueries({
    queries: [eventQueryOptions.templateList()],
  });

  const { data: selectedTemplateData } = useQuery({
    ...eventQueryOptions.templateDetail(selectedTemplateId!),
    enabled: selectedTemplateId !== null,
  });

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

  return (
    <Flex dir="column" gap="8px">
      <Text type="Body" color={theme.colors.gray600}>
        작성했던 템플릿을 불러와 바로 수정할 수 있어요.
      </Text>

      <Dropdown>
        <Dropdown.Trigger>
          <Flex justifyContent="space-between" alignItems="center" width="100%" padding="8px">
            <Text type="Body" color={theme.colors.gray700}>
              {selectedTemplate.length > 25
                ? `${selectedTemplate.slice(0, 25)}...`
                : selectedTemplate}
            </Text>
            <Icon name="dropdownDown" size={16} color="gray500" />
          </Flex>
        </Dropdown.Trigger>

        <Dropdown.Content>
          {templateList && templateList.length > 0 ? (
            templateList.map((template) => (
              <Dropdown.Item
                key={template.templateId}
                onClick={() => {
                  handleTemplateSelect(template.templateId);
                }}
              >
                <Text type="Body" color={theme.colors.gray800}>
                  {template.title.length > 25
                    ? `${template.title.slice(0, 25)}...`
                    : template.title}
                </Text>
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
    </Flex>
  );
};
