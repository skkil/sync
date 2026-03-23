import { useRef } from 'react';

import { cn } from '@/lib/utils';

function Input({ className, type, ...props }: React.ComponentProps<'input'>) {
  return (
    <input
      type={type}
      data-slot="input"
      className={cn(
        'bg-input/30 border-input focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive dark:aria-invalid:border-destructive/50 h-9 rounded-4xl border px-3 py-1 text-base transition-colors file:h-7 file:text-sm file:font-medium focus-visible:ring-[3px] aria-invalid:ring-[3px] md:text-sm file:text-foreground placeholder:text-muted-foreground w-full min-w-0 outline-none file:inline-flex file:border-0 file:bg-transparent disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50',
        className,
      )}
      {...props}
    />
  );
}

type FileInputError = 'size' | 'type';

function FileInput({
  onFileChange,
  accept,
  maxSize = 5 * 1024 * 1024, // 5MB
  maxFiles = 1,
  onError,
  disabled,
  className,
  children,
  ...props
}: Omit<
  React.ComponentProps<'input'>,
  'onError' | 'onChange' | 'type' | 'children'
> & {
  onFileChange?: (files: File[]) => void;
  onError?: (error: FileInputError) => void;
  maxSize?: number;
  maxFiles?: number;
  children?: React.ReactNode;
}) {
  const ref = useRef<HTMLInputElement | null>(null);

  const validateFile = (file: File): FileInputError | null => {
    if (file.size > maxSize) {
      return 'size';
    }

    return null;
  };

  const handleClick = () => {
    if (disabled) {
      return;
    }

    if (ref.current) {
      ref.current.click();
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const fileList = e.target.files;
    if (!fileList || fileList.length === 0) {
      return;
    }

    const files = Array.from(fileList).splice(0, maxFiles);

    const validationResult = files.map((file) => validateFile(file));
    if (validationResult.some((result) => result !== null)) {
      const firstError = validationResult.find((result) => result !== null);
      onError?.(firstError!);
      return;
    }

    onFileChange?.(files);
  };

  return (
    <div>
      <input
        ref={ref}
        type="file"
        accept={accept}
        disabled={disabled}
        onChange={handleChange}
        hidden
        tabIndex={-1}
        {...props}
      />

      <div
        onClick={handleClick}
        className={cn(
          'hover:cursor-pointer',
          disabled && 'cursor-not-allowed opacity-50',
          className,
        )}
      >
        {children}
      </div>
    </div>
  );
}

export type { FileInputError };
export { Input, FileInput };
