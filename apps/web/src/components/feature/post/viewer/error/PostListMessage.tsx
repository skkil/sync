interface PostListMessageProps {
  message: string;
  variant?: 'muted' | 'destructive';
}

export default function PostListMessage({
  message,
  variant = 'muted',
}: PostListMessageProps) {
  return (
    <div className="rounded-md border px-4 py-8 text-center">
      <p
        className={
          variant === 'destructive'
            ? 'text-sm text-destructive'
            : 'text-sm text-muted-foreground'
        }
      >
        {message}
      </p>
    </div>
  );
}
