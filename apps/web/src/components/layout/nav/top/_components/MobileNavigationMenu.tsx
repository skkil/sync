'use client';

import { ListIcon } from '@phosphor-icons/react';
import { useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet';

import UserAvatar from './UserAvatar';

export default function MobileNavigationMenu() {
  const [open, setOpen] = useState(false);

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <Button variant="ghost">
          <ListIcon />
        </Button>
      </SheetTrigger>
      <SheetContent showCloseButton={false}>
        <SheetHeader>
          <SheetTitle>
            <UserAvatar align="start" />
          </SheetTitle>
          <SheetDescription></SheetDescription>
        </SheetHeader>
      </SheetContent>
    </Sheet>
  );
}
