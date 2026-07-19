export interface PostPreviewBodyProps {
  preview: string;
  className?: string;
}

export function PostPreviewBody({ preview, className }: PostPreviewBodyProps) {
  return <p className={className}>{preview}</p>;
}
