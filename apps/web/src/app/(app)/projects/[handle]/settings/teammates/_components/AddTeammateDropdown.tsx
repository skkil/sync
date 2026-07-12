'use client';

import { PlusIcon, UserCircleIcon } from '@phosphor-icons/react';
import { useDebounce } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { useState } from 'react';
import { toast } from 'sonner';

import { CreateProjectInvitationRequestRole } from '@/api/__generated__/types';
import { useSearchUsers } from '@/api/__generated__/user/user';
import { useCreateProjectInvitation } from '@/components/feature/project/hooks/useProjectInvitation';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';

interface AddTeammateDropdownProps {
  projectHandle: string;
}

export default function AddTeammateDropdown({
  projectHandle,
}: AddTeammateDropdownProps) {
  const t = useTranslations(
    'pages.projects.project.settings.teammates.add-dropdown',
  );

  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState('');
  const debouncedQuery = useDebounce(query, 300);

  const { data, isFetching } = useSearchUsers(
    { query: debouncedQuery },
    { query: { enabled: debouncedQuery.trim().length > 0 } },
  );

  const { mutate: createInvitation } = useCreateProjectInvitation();

  const users = data?.data.users ?? [];

  const onSelect = (handle: string) => {
    createInvitation(
      {
        handle: projectHandle,
        data: {
          inviteeHandle: handle,
          role: CreateProjectInvitationRequestRole.Member,
        },
      },
      {
        onSuccess: () => {
          toast.success(t('messages.success'));
          setQuery('');
          setOpen(false);
        },
        onError: () => {
          toast.error(t('messages.error'));
        },
      },
    );
  };

  return (
    <Popover
      open={open}
      onOpenChange={(next) => {
        setOpen(next);
        if (!next) {
          setQuery('');
        }
      }}
    >
      <PopoverTrigger asChild>
        <Button size="sm">
          <PlusIcon className="h-4 w-4" />
          {t('trigger')}
        </Button>
      </PopoverTrigger>

      <PopoverContent className="w-72 p-0" align="end">
        <Command shouldFilter={false}>
          <CommandInput
            placeholder={t('search-placeholder')}
            value={query}
            onValueChange={setQuery}
          />
          <CommandList>
            {query.trim().length === 0 ? (
              <CommandEmpty>{t('empty-query')}</CommandEmpty>
            ) : isFetching ? (
              <CommandEmpty>{t('searching')}</CommandEmpty>
            ) : users.length === 0 ? (
              <CommandEmpty>{t('no-results')}</CommandEmpty>
            ) : (
              <CommandGroup>
                {users.map((user) => (
                  <CommandItem
                    key={user.handle}
                    value={user.handle}
                    onSelect={() => onSelect(user.handle)}
                    className="gap-2"
                  >
                    <Avatar className="h-6 w-6">
                      <AvatarImage src={user.profileImageUrl ?? undefined} />
                      <AvatarFallback>
                        <UserCircleIcon className="h-4 w-4" />
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex flex-col">
                      <span className="text-sm">{user.name}</span>
                      <span className="text-xs text-muted-foreground">
                        @{user.handle}
                      </span>
                    </div>
                  </CommandItem>
                ))}
              </CommandGroup>
            )}
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}
