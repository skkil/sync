import { PostType } from '../types/post';
import PostViewCard, { type PostViewCardProps } from './PostViewCard';

type PostCardProps = Omit<PostViewCardProps, 'variant' | 'type'> & {
  type: PostType;
};

export default function PostCard(props: PostCardProps) {
  return <PostViewCard {...props} variant="detail" />;
}
