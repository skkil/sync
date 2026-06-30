'use client';

import { useEffect } from 'react';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import { useCurrentProject } from '@/hooks/use-current-project';

interface ProjectContextProps {
  handle: string;
}

export default function ProjectContext({ handle }: ProjectContextProps) {
  const { data } = useGetProjectByHandle(handle);
  const { setCurrentProject } = useCurrentProject();

  useEffect(() => {
    if (data) {
      setCurrentProject({ handle, name: data.data.name });
    }
  }, [handle, data, setCurrentProject]);

  return null;
}
