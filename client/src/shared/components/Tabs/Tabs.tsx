import { createContext, useContext, useState, ComponentProps } from 'react';

import { PropsWithRequiredChildren } from '@/shared/utils/utility';

import { StyledTabs, StyledTabsContent, StyledTabsList, StyledTabsTrigger } from './Tabs.styled';

type DivComponentProps = PropsWithRequiredChildren<ComponentProps<'div'>>;
type ButtonComponentProps = PropsWithRequiredChildren<ComponentProps<'button'>>;

type ValueProps = {
  /** Unique identifier for the tab */
  value: string;
};

type DefaultValueProps = {
  /** The value of the tab that should be active by default */
  defaultValue: string;
};

type TabsContextValue = {
  activeTab: string;
  setActiveTab: (value: string) => void;
};

type TabsProps = DivComponentProps & DefaultValueProps;

type TabsListProps = DivComponentProps;

type TabsTriggerProps = ButtonComponentProps & ValueProps;

type TabsContentProps = DivComponentProps & ValueProps;

const TabsContext = createContext<TabsContextValue | null>(null);

const useTabsContext = () => {
  const context = useContext(TabsContext);
  if (!context) {
    throw new Error('Tabs 컴포넌트는 Tabs provider 내부에 위치해야 합니다.');
  }
  return context;
};

export const Tabs = ({ defaultValue, children, ...props }: TabsProps) => {
  const [activeTab, setActiveTab] = useState(defaultValue);

  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      <StyledTabs {...props}>{children}</StyledTabs>
    </TabsContext.Provider>
  );
};

export const TabsList = ({ children, ...props }: TabsListProps) => {
  return (
    <StyledTabsList role="tablist" {...props}>
      {children}
    </StyledTabsList>
  );
};

export const TabsTrigger = ({ value, children, ...props }: TabsTriggerProps) => {
  const { activeTab, setActiveTab } = useTabsContext();
  const isActive = activeTab === value;

  const handleClick = () => {
    setActiveTab(value);
  };

  return (
    <StyledTabsTrigger
      type="button"
      role="tab"
      aria-selected={isActive}
      aria-controls={`tabpanel-${value}`}
      id={`tab-${value}`}
      onClick={handleClick}
      data-active={isActive}
      {...props}
    >
      {children}
    </StyledTabsTrigger>
  );
};

export const TabsContent = ({ value, children, ...props }: TabsContentProps) => {
  const { activeTab } = useTabsContext();
  const isActive = activeTab === value;

  if (!isActive) return null;

  return (
    <StyledTabsContent
      role="tabpanel"
      aria-labelledby={`tab-${value}`}
      id={`tabpanel-${value}`}
      {...props}
    >
      {children}
    </StyledTabsContent>
  );
};

Tabs.Trigger = TabsTrigger;
Tabs.List = TabsList;
Tabs.Content = TabsContent;
