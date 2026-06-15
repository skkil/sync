import { format } from 'date-fns';

import type { GetPostsResponsePostsNodesItemContent } from '@/api/__generated__/types';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import { BaseViewer } from '@/components/ui/editor';
import CommentsSection from '@/features/comment/components/CommentsSection';

interface PostCardProps {
  post: GetPostsResponsePostsNodesItemContent;
}

export default function PostCard({ post }: PostCardProps) {
  return (
    <Card className="mb-4">
      <CardContent>
        <div className="flex items-start gap-4">
          <div className="flex-1 space-y-2">
            <div className="flex items-center justify-between">
              <div>
                <p className="font-semibold text-sm">{post.author?.name}</p>
                <p className="text-xs text-muted-foreground">
                  {format(new Date(post.createdAt), 'PPP')}
                </p>
              </div>

              {post.project?.name && (
                <Badge variant="secondary">{post.project.name}</Badge>
              )}
            </div>

            <BaseViewer content={post.content} />
          </div>
        </div>

        <CommentsSection targetType="POST" targetId={post.id} />
      </CardContent>
    </Card>
  );
}
