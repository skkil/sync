import { cn } from '@/lib/utils';

function Spinner({ className, ...props }: React.ComponentProps<'div'>) {
  return (
    <div
      className={cn(
        'w-3 h-3 border-1 border-black border-t-transparent rounded-full animate-spin',
        className,
      )}
      role="status"
      aria-label="Loading"
      {...props}
    />
  );
}

export { Spinner };
