import PostViewCard, { type PostViewCardProps } from './PostViewCard';

type PostPreviewProps = Omit<PostViewCardProps, 'variant'>;

export default function PostPreview(props: PostPreviewProps) {
  return <PostViewCard {...props} variant="preview" />;
}
