import { createContext, useContext, useState, ComponentProps, ReactNode, useRef } from 'react';

import { StyledTabs, StyledTabsContent, StyledTabsList, StyledTabsTrigger } from './Tabs.styled';

type DivComponentProps = ComponentProps<'div'>;
type ButtonComponentProps = ComponentProps<'button'>;

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
  activeTabIndex: number;
  setActiveTabIndex: (index: number) => void;
};

type TabsProps = DivComponentProps &
  DefaultValueProps & {
    children: ReactNode;
  };

type TabsListProps = DivComponentProps & {
  children: ReactNode;
};

type TabsTriggerProps = ButtonComponentProps &
  ValueProps & {
    children: ReactNode;
  };

type TabsContentProps = DivComponentProps &
  ValueProps & {
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

export const Tabs = ({ defaultValue, children, ...props }: TabsProps) => {
  const [activeTab, setActiveTab] = useState(defaultValue);
  const [activeTabIndex, setActiveTabIndex] = useState(0);

  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab, activeTabIndex, setActiveTabIndex }}>
      <StyledTabs {...props}>{children}</StyledTabs>
    </TabsContext.Provider>
  );
};

export const TabsList = ({ children, ...props }: TabsListProps) => {
  const { activeTabIndex } = useTabsContext();
  const tabCount = Array.isArray(children) ? children.length : 1;

  return (
    <StyledTabsList
      role="tablist"
      style={
        {
          '--active-tab-index': activeTabIndex,
          '--tab-count': tabCount,
        } as React.CSSProperties
      }
      {...props}
    >
      {children}
    </StyledTabsList>
  );
};

export const TabsTrigger = ({ value, children, ...props }: TabsTriggerProps) => {
  const { activeTab, setActiveTab, setActiveTabIndex } = useTabsContext();
  const isActive = activeTab === value;
  const triggerRef = useRef<HTMLButtonElement>(null);

  const handleClick = () => {
    setActiveTab(value);
    if (triggerRef.current) {
      const parent = triggerRef.current.parentElement;
      if (parent) {
        const children = Array.from(parent.children);
        const index = children.indexOf(triggerRef.current);
        setActiveTabIndex(index);
      }
    }
  };

  return (
    <StyledTabsTrigger
      ref={triggerRef}
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
