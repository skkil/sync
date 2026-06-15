'use client';

import { useParams } from 'next/navigation';

import { useGetProjectTeammates } from '@/api/__generated__/project/project';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Separator } from '@/components/ui/separator';

export default function TeammatesSettingsPage() {
  const { handle } = useParams<{ handle: string }>();
  const { data, isPending } = useGetProjectTeammates(handle);

  return (
    <div className="max-w-2xl space-y-6">
      <div>
        <h2 className="text-lg font-semibold">Teammates</h2>
        <p className="text-sm text-muted-foreground">
          Manage the members of this project.
        </p>
      </div>

      <Separator />

      {isPending ? (
        <TeammatesListSkeleton />
      ) : (
        <div className="space-y-3">
          {data?.data.teammates.map((teammate) => (
            <div
              key={teammate.handle}
              className="flex items-center justify-between"
            >
              <div className="flex items-center gap-3">
                <Avatar>
                  <AvatarFallback>
                    {teammate.name.charAt(0).toUpperCase()}
                  </AvatarFallback>
                </Avatar>
                <div>
                  <p className="text-sm font-medium">{teammate.name}</p>
                  <p className="text-xs text-muted-foreground">
                    @{teammate.handle}
                  </p>
                </div>
              </div>
              <span className="text-xs text-muted-foreground capitalize">
                {teammate.role.toLowerCase()}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function TeammatesListSkeleton() {
  return (
    <div className="space-y-3">
      {Array.from({ length: 3 }).map((_, i) => (
        <div key={i} className="flex items-center gap-3">
          <div className="h-8 w-8 rounded-full bg-muted animate-pulse" />
          <div className="space-y-1">
            <div className="h-3 w-24 rounded bg-muted animate-pulse" />
            <div className="h-3 w-16 rounded bg-muted animate-pulse" />
          </div>
        </div>
      ))}
    </div>
  );
}
