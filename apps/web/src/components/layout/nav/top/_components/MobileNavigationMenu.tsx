import { BriefcaseIcon, ListIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useState } from 'react';

import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
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
  const t = useTranslations('components.navigation.menu');

  const [open, setOpen] = useState(false);

  const groups = [
    {
      id: 'home',
      tabs: [
        {
          icon: <BriefcaseIcon />,
          href: '/jobs',
          label: t('jobs'),
        },
      ],
    },
  ];

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

          {groups.map((group) => (
            <div key={group.id} className="space-y-2">
              <div>
                {group.tabs.map((tab) => (
                  <Link
                    key={tab.label}
                    href={tab.href}
                    className="flex items-center gap-2"
                    onClick={() => setOpen(false)}
                  >
                    {tab.icon}
                    {tab.label}
                  </Link>
                ))}
              </div>
              <Separator />
            </div>
          ))}
        </SheetHeader>
      </SheetContent>
    </Sheet>
  );
}
