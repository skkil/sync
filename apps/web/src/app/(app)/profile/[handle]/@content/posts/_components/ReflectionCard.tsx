import { format } from 'date-fns';

import type { GetReflectionsResponseReflectionsNodesItemContent } from '@/api/__generated__/types';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import { BaseViewer } from '@/components/ui/editor';

interface ReflectionCardProps {
  reflection: GetReflectionsResponseReflectionsNodesItemContent;
}

export default function ReflectionCard({ reflection }: ReflectionCardProps) {
  return (
    <Card className="mb-4">
      <CardContent>
        <div className="flex items-start gap-4">
          <div className="flex-1 space-y-2">
            <div className="flex items-center justify-between">
              <div>
                <p className="font-semibold text-sm">
                  {reflection.author?.name}
                </p>
                <p className="text-xs text-muted-foreground">
                  {format(new Date(reflection.createdAt), 'PPP')}
                </p>
              </div>

              {reflection.project?.name && (
                <Badge variant="secondary">{reflection.project.name}</Badge>
              )}
            </div>

            <BaseViewer content={reflection.content} />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
