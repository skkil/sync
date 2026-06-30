'use client';

import { FolderSimpleIcon, PlusIcon } from '@phosphor-icons/react';
import Link from 'next/link';

import { useGetProjectsByUser } from '@/api/__generated__/project/project';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { isAuthenticated } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';

export default function Projects() {
  return (
    <div className="mx-auto max-w-2xl px-4 py-8">
      <MyProjects />
    </div>
  );
}

function MyProjects() {
  const { data: session } = useSession();
  const { data: projects, isPending: isGetProjectsByUserPending } =
    useGetProjectsByUser(session?.user.handle || '', {
      query: {
        enabled: !!session?.user.handle,
      },
    });

  if (!isAuthenticated(session)) {
    return null;
  }

  if (isGetProjectsByUserPending) {
    return (
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <Skeleton className="h-6 w-28" />
          <Skeleton className="h-9 w-9 rounded-md" />
        </div>
        <div className="space-y-2">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-16 w-full rounded-2xl" />
          ))}
        </div>
      </div>
    );
  }

  if (!projects) {
    return null;
  }

  const { data: projectsData } = projects;

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-base font-semibold">My Projects</h2>
        <Button asChild variant="ghost" size="icon">
          <Link href="/projects/new">
            <PlusIcon />
          </Link>
        </Button>
      </div>

      {projectsData.projects.length === 0 ? (
        <Card className="items-center justify-center py-12 text-center">
          <FolderSimpleIcon className="text-muted-foreground mx-auto mb-3 size-10" />
          <p className="text-muted-foreground text-sm">No projects yet.</p>
          <Button asChild variant="outline" size="sm" className="mt-4">
            <Link href="/projects/new">Create your first project</Link>
          </Button>
        </Card>
      ) : (
        <div className="space-y-2">
          {projectsData.projects.map((project) => (
            <Link key={project.id} href={`/projects/${project.handle}`}>
              <Card
                size="sm"
                className="hover:bg-accent/50 cursor-pointer flex-row items-center gap-3 px-4 transition-colors"
              >
                <div className="bg-muted flex size-8 shrink-0 items-center justify-center rounded-lg">
                  <FolderSimpleIcon className="size-4" />
                </div>
                <div className="min-w-0">
                  <p className="truncate font-medium">{project.name}</p>
                  <p className="text-muted-foreground truncate text-xs">
                    @{project.handle}
                  </p>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
