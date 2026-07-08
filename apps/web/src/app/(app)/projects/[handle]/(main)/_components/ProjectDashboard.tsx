'use client';

import { ChatCircleIcon, HeartIcon, UsersIcon } from '@phosphor-icons/react';

import { Card, CardContent } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';

const STATS = [
  { label: '게시물', icon: ChatCircleIcon },
  { label: '팔로워', icon: HeartIcon },
  { label: '팀원', icon: UsersIcon },
];

export default function ProjectDashboard() {
  return (
    <div className="space-y-6 pt-4">
      <StatsSection />
      <RecentActivitySection />
      <TeammatesSection />
    </div>
  );
}

function StatsSection() {
  return (
    <div className="grid grid-cols-3 gap-3">
      {STATS.map(({ label, icon: Icon }) => (
        <Card key={label} size="sm">
          <CardContent className="flex flex-col gap-2">
            <div className="flex items-center gap-1.5 text-muted-foreground">
              <Icon className="size-4" />
              <span className="text-xs">{label}</span>
            </div>
            <Skeleton className="h-7 w-12" />
          </CardContent>
        </Card>
      ))}
    </div>
  );
}

function RecentActivitySection() {
  return (
    <section className="space-y-3">
      <h2 className="text-sm font-medium">최근 활동</h2>
      <div className="space-y-3">
        {Array.from({ length: 3 }).map((_, index) => (
          <Card key={index} size="sm">
            <CardContent className="flex items-center gap-3">
              <Skeleton className="size-9 shrink-0 rounded-full" />
              <div className="flex-1 space-y-2">
                <Skeleton className="h-4 w-3/4" />
                <Skeleton className="h-3 w-1/2" />
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </section>
  );
}

function TeammatesSection() {
  return (
    <section className="space-y-3">
      <h2 className="text-sm font-medium">팀원</h2>
      <div className="flex flex-wrap gap-4">
        {Array.from({ length: 4 }).map((_, index) => (
          <div key={index} className="flex flex-col items-center gap-2">
            <Skeleton className="size-12 rounded-full" />
            <Skeleton className="h-3 w-12" />
          </div>
        ))}
      </div>
    </section>
  );
}
