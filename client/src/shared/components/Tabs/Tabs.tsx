import { createContext, useContext, useState, ReactNode } from 'react';

import { SerializedStyles } from '@emotion/react';

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
  /** Custom styles using emotion CSS-in-JS */
  css?: SerializedStyles;
};

type TabsListProps = {
  /** TabsTrigger components as children */
  children: ReactNode;
  /** Custom styles using emotion CSS-in-JS */
  css?: SerializedStyles;
};

type TabsTriggerProps = {
  /** Unique identifier for the tab (must match the corresponding TabsContent value) */
  value: string;
  /** Content to display in the tab button */
  children: ReactNode;
  /** Custom styles using emotion CSS-in-JS */
  css?: SerializedStyles;
};

type TabsContentProps = {
  /** Unique identifier for the tab (must match the corresponding TabsTrigger value) */
  value: string;
  /** Content to display when the tab is active */
  children: ReactNode;
  /** Custom styles using emotion CSS-in-JS */
  css?: SerializedStyles;
};

const TabsContext = createContext<TabsContextValue | null>(null);

const useTabsContext = () => {
  const context = useContext(TabsContext);
  if (!context) {
    throw new Error('Tabs 컴포넌트는 Tabs provider 내부에 위치해야 합니다.');
  }
  return context;
};

export const Tabs = ({ defaultValue, css: cssProp, children }: TabsProps) => {
  const [activeTab, setActiveTab] = useState(defaultValue || '');

  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      <StyledTabs css={cssProp}>{children}</StyledTabs>
    </TabsContext.Provider>
  );
};

export const TabsList = ({ css: cssProp, children }: TabsListProps) => {
  return (
    <StyledTabsList css={cssProp} role="tablist">
      {children}
    </StyledTabsList>
  );
};

export const TabsTrigger = ({ value, children, css: cssProp }: TabsTriggerProps) => {
  const { activeTab, setActiveTab } = useTabsContext();
  const isActive = activeTab === value;

  const handleClick = () => {
    setActiveTab(value);
  };

  return (
    <StyledTabsTrigger
      css={cssProp}
      role="tab"
      aria-selected={isActive}
      aria-controls={`tabpanel-${value}`}
      id={`tab-${value}`}
      onClick={handleClick}
      data-active={isActive}
    >
      {children}
    </StyledTabsTrigger>
  );
};

export const TabsContent = ({ value, css: cssProp, children }: TabsContentProps) => {
  const { activeTab } = useTabsContext();
  const isActive = activeTab === value;

  if (!isActive) return null;

  return (
    <StyledTabsContent
      css={cssProp}
      role="tabpanel"
      aria-labelledby={`tab-${value}`}
      id={`tabpanel-${value}`}
    >
      {children}
    </StyledTabsContent>
  );
};
