'use client';

import { SidebarIcon, UserIcon, XIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { forwardRef, useEffect, useRef, useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { useModal } from '@/hooks/store';
import { cn } from '@/lib/utils';

import AccountSettingsComponent from './_components/AccountSettings';

type SettingsCategory = 'account';

interface Category {
  id: SettingsCategory;
  icon: React.ReactNode;
  component: ReturnType<typeof forwardRef<SettingsCategoryRef>>;
}

const categories: Category[] = [
  {
    id: 'account',
    icon: <UserIcon />,
    component: AccountSettingsComponent,
  },
];

export interface SettingsCategoryRef {
  submit: () => void;
  reset: () => void;
}

export default function SettingsModal() {
  const t = useTranslations('modals.settings');

  const { isOpen, closeModal } = useModal();
  const [selectedCategory, setSelectedCategory] = useState<SettingsCategory>(
    categories[0]?.id || 'account',
  );
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [isMobile, setIsMobile] = useState(false);

  const contentRef = useRef<SettingsCategoryRef>(null);

  // Detect screen size and set mobile state
  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth < 768); // md breakpoint
    };

    handleResize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const onCancel = () => {
    contentRef.current?.reset();
    closeModal();
  };

  const onSubmit = () => {
    contentRef.current?.submit();
  };

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const closeSidebar = () => {
    setIsSidebarOpen(false);
  };

  return (
    <Dialog
      open={isOpen}
      onOpenChange={(open) => {
        if (!open) {
          closeModal();
        }
      }}
    >
      <DialogContent className="w-11/12 sm:max-w-3xl md:max-w-4xl lg:max-w-5xl h-[90vh] max-h-[90vh] p-0 overflow-hidden">
        <div className="flex h-full relative">
          {/* Backdrop overlay for mobile */}
          {isMobile && isSidebarOpen && (
            <div
              className="absolute inset-0 bg-black/50 z-40"
              onClick={closeSidebar}
            />
          )}

          <aside
            className={cn(
              'bg-muted border-border flex flex-col border-r transition-transform duration-300 ease-in-out z-50',
              // Desktop: always visible, fixed width
              'md:relative md:translate-x-0 md:w-64',
              // Mobile: overlay sidebar
              'absolute inset-y-0 left-0 p-6',
              // Small screens: full width
              'w-full sm:w-64',
              // Toggle visibility on mobile
              isMobile
                ? isSidebarOpen
                  ? 'translate-x-0'
                  : '-translate-x-full'
                : 'translate-x-0',
            )}
          >
            {isMobile && (
              <Button
                variant="ghost"
                onClick={closeSidebar}
                className="absolute top-4 right-4 p-1 rounded-md hover:bg-background"
              >
                <XIcon />
              </Button>
            )}

            <h1 className="text-lg mb-5">{t('title')}</h1>
            <div className="flex flex-col gap-1">
              {categories.map((category) => (
                <Button
                  key={category.id}
                  onClick={() => {
                    setSelectedCategory(category.id);
                    if (isMobile) {
                      closeSidebar();
                    }
                  }}
                  variant="ghost"
                  className={cn(
                    'hover:bg-muted flex items-center justify-start',
                    selectedCategory === category.id
                      ? 'bg-background text-foreground'
                      : 'text-muted-foreground',
                  )}
                >
                  {category.icon}
                  {t(`categories.${category.id}.label`)}
                </Button>
              ))}
            </div>
          </aside>

          <div className="flex flex-1 flex-col overflow-hidden">
            <DialogHeader className="border-border border-b p-6">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                  {/* Hamburger menu button for mobile */}
                  {isMobile && (
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={toggleSidebar}
                      className="md:hidden"
                    >
                      <SidebarIcon />
                    </Button>
                  )}

                  <div className="flex flex-col gap-2">
                    <DialogTitle>
                      {t(`categories.${selectedCategory}.title`)}
                    </DialogTitle>
                    <DialogDescription>
                      {t(`categories.${selectedCategory}.description`)}
                    </DialogDescription>
                  </div>
                </div>

                <DialogClose />
              </div>
            </DialogHeader>

            <div className="flex-1 overflow-y-auto p-6">
              {categories.map((category) =>
                category.id === selectedCategory ? (
                  <category.component key={category.id} ref={contentRef} />
                ) : null,
              )}
            </div>

            <DialogFooter className="border-border border-t p-6">
              <div className="flex gap-2">
                <Button variant="outline" onClick={onCancel}>
                  {t('actions.cancel')}
                </Button>
                <Button onClick={onSubmit}>{t('actions.save')}</Button>
              </div>
            </DialogFooter>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
