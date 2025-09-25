import {
  createContext,
  useContext,
  useState,
  ComponentProps,
  ReactNode,
  Children,
  ReactElement,
  isValidElement,
  useEffect,
  useLayoutEffect,
  useRef,
} from 'react';

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

  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      <StyledTabs {...props}>{children}</StyledTabs>
    </TabsContext.Provider>
  );
};

export const TabsList = ({ children, ...props }: TabsListProps) => {
  const { activeTab } = useTabsContext();
  const listRef = useRef<HTMLDivElement | null>(null);

  const tabValues = Children.toArray(children)
    .filter((child): child is ReactElement<{ value: string }> => isValidElement(child))
    .map((child) => child.props?.value);

  const activeTabIndex = Math.max(0, tabValues.indexOf(activeTab));
  const tabCount = tabValues.length;

  const updateUnderline = () => {
    const listEl = listRef.current;
    if (!listEl) return;
    const btn = listEl.querySelector<HTMLButtonElement>(`#tab-${activeTab}`);
    if (!btn) return;

    const x = btn.offsetLeft - listEl.scrollLeft;
    const w = btn.offsetWidth;

    listEl.style.setProperty('--underline-x', `${x}px`);
    listEl.style.setProperty('--underline-w', `${w}px`);
  };

  useLayoutEffect(updateUnderline, [activeTab, children]);
  useEffect(() => {
    const listEl = listRef.current;
    if (!listEl) return;

    const onResize = () => updateUnderline();
    const onScroll = () => updateUnderline();

    window.addEventListener('resize', onResize);
    listEl.addEventListener('scroll', onScroll, { passive: true });

    requestAnimationFrame(updateUnderline);

    return () => {
      window.removeEventListener('resize', onResize);
      listEl.removeEventListener('scroll', onScroll);
    };
  }, [activeTab, children]);

  return (
    <StyledTabsList
      ref={listRef}
      role="tablist"
      tabCount={tabCount}
      activeTabIndex={activeTabIndex}
      {...props}
    >
      {children}
    </StyledTabsList>
  );
};

export const TabsTrigger = ({ value, children, ...props }: TabsTriggerProps) => {
  const { activeTab, setActiveTab } = useTabsContext();
  const isActive = activeTab === value;

  return (
    <StyledTabsTrigger
      type="button"
      role="tab"
      aria-selected={isActive}
      aria-controls={`tabpanel-${value}`}
      id={`tab-${value}`}
      onClick={() => setActiveTab(value)}
      data-active={isActive}
      {...props}
    >
      {children}
    </StyledTabsTrigger>
  );
};

export const TabsContent = ({ value, children, ...props }: TabsContentProps) => {
  const { activeTab } = useTabsContext();
  if (activeTab !== value) return null;

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
