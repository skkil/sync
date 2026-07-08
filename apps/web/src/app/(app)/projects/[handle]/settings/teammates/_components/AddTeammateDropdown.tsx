'use client';

import { PlusIcon, UserCircleIcon } from '@phosphor-icons/react';
import { useDebounce } from '@uidotdev/usehooks';
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
          toast.success('초대를 보냈습니다.');
          setQuery('');
          setOpen(false);
        },
        onError: () => {
          toast.error('초대에 실패했습니다.');
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
          팀원 추가
        </Button>
      </PopoverTrigger>

      <PopoverContent className="w-72 p-0" align="end">
        <Command shouldFilter={false}>
          <CommandInput
            placeholder="이름 또는 핸들로 검색"
            value={query}
            onValueChange={setQuery}
          />
          <CommandList>
            {query.trim().length === 0 ? (
              <CommandEmpty>검색어를 입력하세요.</CommandEmpty>
            ) : isFetching ? (
              <CommandEmpty>검색하는 중...</CommandEmpty>
            ) : users.length === 0 ? (
              <CommandEmpty>사용자를 찾을 수 없습니다.</CommandEmpty>
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
