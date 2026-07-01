'use client';

import { XIcon } from '@phosphor-icons/react';

import { Button } from '@/components/ui/button';
import { useSidebar } from '@/components/ui/sidebar';
import { useIsMobile } from '@/hooks/use-mobile';

export default function SidebarCloseButton() {
  const { setOpenMobile } = useSidebar();
  const isMobile = useIsMobile();

  if (!isMobile) return null;

  return (
    <Button
      variant="ghost"
      size="icon"
      className="size-7 shrink-0"
      onClick={() => setOpenMobile(false)}
    >
      <XIcon />
    </Button>
  );
}
