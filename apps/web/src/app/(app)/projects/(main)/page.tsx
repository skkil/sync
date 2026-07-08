import { requireSession } from '@/lib/auth/guards';

import ProjectsTabs from './_components/ProjectsTabs';

export default async function Projects() {
  await requireSession();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Your Workspaces</h1>
      </div>

      <ProjectsTabs />
    </div>
  );
}
