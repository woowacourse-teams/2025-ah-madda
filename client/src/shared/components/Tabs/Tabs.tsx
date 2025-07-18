import { createContext, useContext, useState, ReactNode, ComponentProps } from 'react';

import { StyledTabs, StyledTabsList, StyledTabsTrigger, StyledTabsContent } from './Tabs.styled';

type TabsContextValue = {
  activeTab: string;
  setActiveTab: (value: string) => void;
};

type TabsProps = {
  /** The value of the tab that should be active by default */
  defaultValue?: string;
  /** Child components including TabsList and TabsContent */
  children: ReactNode;
};

type TabsListProps = {
  /** TabsTrigger components as children */
  children: ReactNode;
};

type TabsTriggerProps = {
  /** Unique identifier for the tab (must match the corresponding TabsContent value) */
  value: string;
  /** Content to display in the tab button */
  children: ReactNode;
} & ComponentProps<'button'>;

type TabsContentProps = {
  /** Unique identifier for the tab (must match the corresponding TabsContent value) */
  value: string;
  /** Content to display when the tab is active */
  children: ReactNode;
};

const TabsContext = createContext<TabsContextValue | null>(null);

const useTabsContext = () => {
  const context = useContext(TabsContext);
  if (!context) {
    throw new Error('Tabs 컴포넌트는 Tabs provider 내부에 위치해야 합니다.');
  }
  return context;
};

export const Tabs = ({ defaultValue, children }: TabsProps) => {
  const [activeTab, setActiveTab] = useState(defaultValue || '');

  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      <StyledTabs>{children}</StyledTabs>
    </TabsContext.Provider>
  );
};

export const TabsList = ({ children }: TabsListProps) => {
  return <StyledTabsList role="tablist">{children}</StyledTabsList>;
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

export const TabsContent = ({ value, children }: TabsContentProps) => {
  const { activeTab } = useTabsContext();
  const isActive = activeTab === value;

  if (!isActive) return null;

  return (
    <StyledTabsContent role="tabpanel" aria-labelledby={`tab-${value}`} id={`tabpanel-${value}`}>
      {children}
    </StyledTabsContent>
  );
};

Tabs.Trigger = TabsTrigger;
Tabs.List = TabsList;
Tabs.Content = TabsContent;
