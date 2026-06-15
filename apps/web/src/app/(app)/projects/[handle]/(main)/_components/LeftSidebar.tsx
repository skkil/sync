'use client';

import { GearIcon } from '@phosphor-icons/react';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import { GetProjectResponse } from '@/api/__generated__/types';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';

interface LeftSidebarProps {
  handle: string;
}

export default function LeftSidebar({ handle }: LeftSidebarProps) {
  const { data, isPending } = useGetProjectByHandle(handle);

  if (isPending) {
    return <LeftSidebarSkeleton />;
  }

  if (!data) {
    return null;
  }

  const { data: project } = data;

  return (
    <div className="h-full flex flex-col justify-between">
      <div className="flex flex-col gap-4">
        <AboutCard project={project} />

        <Separator />

        <TeammatesCard project={project} />
      </div>
    </div>
  );
}

// TODO
function LeftSidebarSkeleton() {
  return null;
}

function AboutCard({ project }: { project: GetProjectResponse }) {
  return (
    <div>
      <div className="flex items-center justify-between">
        <span>About</span>
        <div>
          {project.role === 'ADMIN' && (
            <Button variant="ghost" size="icon">
              <GearIcon />
            </Button>
          )}
        </div>
      </div>

      <div>
        {project.description || (
          <span className="italic">No description provided.</span>
        )}
      </div>
    </div>
  );
}

function TeammatesCard({ project }: { project: GetProjectResponse }) {
  return (
    <div>
      <div className="mb-2">Teammates</div>

      <div>
        {project.teammates.map((teammate) => (
          <div key={teammate.handle}>
            <Avatar>
              <AvatarFallback />
            </Avatar>
          </div>
        ))}
        {project.hasMoreTeammates && <span>And more...</span>}
      </div>
    </div>
  );
}
