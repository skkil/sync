import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { cn } from '@/lib/utils';

interface ProjectAvatarProps {
  name: string;
  iconUrl?: string | null;
  size?: 'default' | 'sm' | 'lg';
  className?: string;
}

function ProjectAvatar({
  name,
  iconUrl,
  size = 'default',
  className,
}: ProjectAvatarProps) {
  return (
    <Avatar size={size} className={cn('rounded-lg', className)}>
      <AvatarImage src={iconUrl || undefined} alt={name} />
      <AvatarFallback className="rounded-lg bg-primary font-semibold text-primary-foreground">
        {name.charAt(0).toUpperCase()}
      </AvatarFallback>
    </Avatar>
  );
}

export { ProjectAvatar };
