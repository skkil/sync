'use client';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import { GetProjectResponse } from '@/api/__generated__/types';
import { Separator } from '@/components/ui/separator';

interface RightSidebarProps {
  handle: string;
}

export default function RightSidebar({ handle }: RightSidebarProps) {
  const { data, isPending } = useGetProjectByHandle(handle);

  if (isPending) {
    return null;
  }

  if (!data) {
    return null;
  }

  const { data: project } = data;

  return (
    <div>
      <AskAICard project={project} />

      {project.recentActivities && (
        <>
          <Separator />
          <RecentActivities project={project} />
        </>
      )}
    </div>
  );
}

function AskAICard({ project }: { project: GetProjectResponse }) {
  return (
    <div>
      <h3 className="text-sm font-semibold mb-2">Ask AI</h3>
      <p className="text-xs text-muted-foreground mb-3">
        Ask anything about <span className="font-medium">{project.name}</span>.
      </p>
      <input
        className="w-full rounded-md border border-border bg-muted px-3 py-2 text-sm placeholder:text-muted-foreground focus:outline-none"
        placeholder="Ask a question..."
        disabled
      />
    </div>
  );
}

function RecentActivities({ project }: { project: GetProjectResponse }) {
  if (!project.recentActivities) {
    return null;
  }

  return (
    <div>
      <h3 className="text-sm font-semibold mb-3">Recent Activity</h3>
      <ul className="space-y-3">
        {project.recentActivities.map((a) => (
          <li key={a.id} className="flex items-center justify-between text-xs">
            <span className="text-foreground">{a.text}</span>
            <span className="text-muted-foreground">{a.timestamp}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}
