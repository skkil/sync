'use client';

import Link from 'next/link';

import { useGetProjectsByUser } from '@/api/__generated__/project/project';
import { isAuthenticated } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';

export default function Projects() {
  return (
    <div>
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
    return <div>Loading...</div>;
  }

  if (!projects) {
    return null;
  }

  const { data: projectsData } = projects;

  return (
    <div>
      <span>My Projects</span>
      <div>
        {projectsData.projects.length === 0 ? (
          <div>No projects found.</div>
        ) : (
          projectsData.projects.map((project) => (
            <Link key={project.id} href={`/projects/${project.handle}`}>
              <div>
                <h3>{project.name}</h3>
              </div>
            </Link>
          ))
        )}
      </div>
    </div>
  );
}
