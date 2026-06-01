'use client';

import { useDebounce } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { type KeyboardEvent, useRef, useState } from 'react';
import { toast } from 'sonner';

import {
  useCreateProject,
  useSearchProjects,
} from '@/api/__generated__/project/project';
import { cn } from '@/lib/utils';

const DEBOUNCE_MS = 300;
const MIN_PROJECT_NAME_LENGTH = 6;

interface ProjectInputProps {
  projectId: number | null;
  onChange: (projectId: number | null) => void;
}

export function ProjectInput({ projectId, onChange }: ProjectInputProps) {
  const t = useTranslations('components.editor.projects');

  const inputRef = useRef<HTMLInputElement>(null);
  const [input, setInput] = useState('');
  const [selectedProjectName, setSelectedProjectName] = useState<string | null>(
    null,
  );
  const debouncedInput = useDebounce(input, DEBOUNCE_MS);

  const [focused, setFocused] = useState(false);
  const [open, setOpen] = useState(false);

  const { data, isFetching } = useSearchProjects(
    { query: debouncedInput },
    {
      query: {
        enabled: debouncedInput.trim().length > 0,
      },
    },
  );

  const { mutate: createProject, isPending: isCreating } = useCreateProject();

  const suggestions = data?.data?.projects ?? [];
  const showDropdown = open && focused && input.trim().length > 0;
  const canCreateProject = input.trim().length >= MIN_PROJECT_NAME_LENGTH;

  const isPending = input !== debouncedInput || isFetching;

  const selectProject = (id: number, name: string) => {
    setSelectedProjectName(name);
    onChange(id);
    setInput('');
    setOpen(false);
  };

  const handleCreateProject = () => {
    if (!input.trim()) {
      return;
    }

    createProject(
      { data: { name: input.trim() } },
      {
        onSuccess: ({ data: { projectId } }) => {
          selectProject(projectId, input.trim());
          toast.success(t('created'));
        },
        onError: () => {
          toast.error(t('create-error'));
        },
      },
    );
  };

  const removeProject = () => {
    setSelectedProjectName(null);
    onChange(null);
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Escape') {
      setOpen(false);
    } else if (
      e.key === 'Backspace' &&
      input === '' &&
      selectedProjectName !== null
    ) {
      removeProject();
    }
  };

  return (
    <div className="relative flex flex-col gap-2">
      <div
        className="flex flex-wrap items-center gap-1.5 rounded-md border border-input bg-transparent px-3 py-2 text-sm cursor-text min-h-9"
        onClick={() => inputRef.current?.focus()}
      >
        {selectedProjectName && (
          <button
            onClick={(e) => {
              e.stopPropagation();
              removeProject();
            }}
            className="inline-flex items-center gap-1 rounded bg-primary px-2 py-0.5 text-lg font-medium text-primary-foreground"
          >
            {selectedProjectName}
            <span className="text-xs">×</span>
          </button>
        )}

        {!selectedProjectName && (
          <input
            ref={inputRef}
            value={input}
            onChange={(e) => {
              setInput(e.target.value);
              setOpen(true);
            }}
            onKeyDown={handleKeyDown}
            onFocus={() => {
              setFocused(true);
              if (input.length > 0) setOpen(true);
            }}
            onBlur={() => {
              setFocused(false);
              setTimeout(() => setOpen(false), 150);
            }}
            placeholder={t('placeholder')}
            className="flex-1 min-w-24 bg-transparent outline-none placeholder:text-muted-foreground text-lg"
          />
        )}
      </div>

      {showDropdown && (
        <div className="absolute top-full left-0 right-0 z-50 mt-1 rounded-md border bg-popover shadow-md overflow-hidden min-h-10">
          {isPending || isCreating ? (
            <div className="flex items-center justify-center px-3 py-3 text-sm text-muted-foreground">
              {isCreating ? t('creating') : t('loading')}
            </div>
          ) : suggestions.length === 0 ? (
            <div className="flex flex-col">
              <div className="flex flex-col items-center justify-center px-3 py-3 text-sm text-muted-foreground">
                <span>{t('not-found')}</span>

                {canCreateProject && (
                  <button
                    type="button"
                    onMouseDown={(e) => {
                      e.preventDefault();
                      handleCreateProject();
                    }}
                    className="w-full flex items-center justify-center px-3 py-2 text-sm hover:underline text-left transition-colors"
                  >
                    <span className="font-medium text-primary">
                      {t('create-new', { name: input.trim() })}
                    </span>
                  </button>
                )}
              </div>
            </div>
          ) : (
            suggestions.map((project) => (
              <button
                key={project.id}
                type="button"
                onMouseDown={(e) => {
                  e.preventDefault();
                  selectProject(project.id, project.name);
                }}
                className={cn(
                  'w-full flex items-center justify-between px-3 py-2 text-sm hover:bg-accent text-left transition-colors',
                  projectId === project.id && 'opacity-50 cursor-not-allowed',
                )}
              >
                <span className="font-medium">{project.name}</span>
              </button>
            ))
          )}
        </div>
      )}
    </div>
  );
}
