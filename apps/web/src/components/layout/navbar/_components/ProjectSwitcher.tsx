'use client';

import { CaretDownIcon } from '@phosphor-icons/react';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

import { useSearchMyProjects } from '@/api/__generated__/project/project';
import { Button } from '@/components/ui/button';
import {
  Command,
  CommandEmpty,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import { useCurrentProject } from '@/hooks/use-current-project';
import { useSession } from '@/lib/auth/client';

export default function ProjectSwitcher() {
  const router = useRouter();
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState('');

  const { data: session } = useSession();
  const { currentProject, setCurrentProject } = useCurrentProject();
  const { data } = useSearchMyProjects(
    { query },
    { query: { enabled: !!session } },
  );

  const projects = data?.data.projects ?? [];

  if (!session) {
    return (
      <Button
        variant="ghost"
        className="flex items-center gap-1 px-2 h-8 text-sm font-medium text-muted-foreground"
        aria-label="내 프로젝트"
        disabled
      >
        프로젝트
        <CaretDownIcon size={12} className="shrink-0" />
      </Button>
    );
  }

  return (
    <Popover
      open={open}
      onOpenChange={(next) => {
        setOpen(next);
        if (!next) setQuery('');
      }}
    >
      <PopoverTrigger asChild>
        {currentProject ? (
          <Button
            variant="ghost"
            className="flex items-center gap-1 px-2 h-8 text-sm font-medium max-w-48"
            aria-label="프로젝트 전환"
          >
            <span className="truncate">{currentProject.name}</span>
            <CaretDownIcon
              size={12}
              className="shrink-0 text-muted-foreground"
            />
          </Button>
        ) : (
          <Button
            variant="ghost"
            className="flex items-center gap-1 px-2 h-8 text-sm font-medium text-muted-foreground"
            aria-label="내 프로젝트"
          >
            프로젝트
            <CaretDownIcon size={12} className="shrink-0" />
          </Button>
        )}
      </PopoverTrigger>

      <PopoverContent className="w-64 p-0" align="start">
        <Command shouldFilter={false}>
          <CommandInput
            placeholder="프로젝트 검색..."
            value={query}
            onValueChange={setQuery}
          />
          <CommandList>
            {projects.length === 0 ? (
              <CommandEmpty>프로젝트가 없습니다.</CommandEmpty>
            ) : (
              projects.map((project) => (
                <CommandItem
                  key={project.id}
                  onSelect={() => {
                    setCurrentProject({
                      handle: project.handle,
                      name: project.name,
                    });
                    router.push(`/projects/${project.handle}`);
                    setOpen(false);
                  }}
                >
                  {project.name}
                </CommandItem>
              ))
            )}
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}
