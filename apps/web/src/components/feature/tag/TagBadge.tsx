'use client';

import { FolderSimpleIcon, XIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { ComponentProps } from 'react';

import { Badge } from '@/components/ui/badge';
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import { cn } from '@/lib/utils';

interface TagBadgeProps extends ComponentProps<typeof Badge> {
  name: string;
  isProjectTag?: boolean;
}

function ProjectTagIndicator() {
  const t = useTranslations('components.editor.tags');

  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <FolderSimpleIcon weight="fill" />
      </TooltipTrigger>
      <TooltipContent>{t('project-tag')}</TooltipContent>
    </Tooltip>
  );
}

export function TagBadge({ name, isProjectTag, ...props }: TagBadgeProps) {
  return (
    <Badge {...props}>
      {isProjectTag && <ProjectTagIndicator />}
      {name}
    </Badge>
  );
}

interface RemovableTagBadgeProps extends TagBadgeProps {
  onRemove: () => void;
}

export function RemovableTagBadge({
  name,
  isProjectTag,
  onRemove,
  variant = 'secondary',
  className,
  ...props
}: RemovableTagBadgeProps) {
  return (
    <button
      type="button"
      onClick={(event) => {
        event.stopPropagation();
        onRemove();
      }}
    >
      <Badge variant={variant} className={cn('gap-1', className)} {...props}>
        {isProjectTag && <ProjectTagIndicator />}
        {name}
        <XIcon size={10} />
      </Badge>
    </button>
  );
}
