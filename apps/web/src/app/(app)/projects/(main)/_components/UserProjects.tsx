'use client';

import { useGetProjectsByUser } from '@/api/__generated__/project/project';
import { NewProjectCard, ProjectCard } from '@/components/feature/project/card';
import { useSession } from '@/lib/auth/client';

export default function UserProjects() {
  const { data: session } = useSession();

  const { data: projectsData, isPending } = useGetProjectsByUser(
    session?.user.handle || '',
    {
      query: {
        enabled: !!session?.user.handle,
      },
    },
  );

  if (isPending) {
    return null;
  }

  if (!projectsData) {
    return null;
  }

  const { projects } = projectsData.data;

  return (
    <div className="grid grid-cols-1 gap-4 pt-4 sm:grid-cols-2 lg:grid-cols-3">
      {projects.map((project) => (
        <ProjectCard
          key={project.handle}
          name={project.name}
          handle={project.handle}
          iconUrl={project.iconUrl}
        />
      ))}
      <NewProjectCard />
    </div>
  );
}
