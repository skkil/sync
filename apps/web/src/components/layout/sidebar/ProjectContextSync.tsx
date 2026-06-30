'use client';

import { usePathname } from 'next/navigation';
import { useEffect } from 'react';

import { useCurrentProject } from '@/hooks/use-current-project';

export default function ProjectContextSync() {
  const pathname = usePathname();
  const { clearCurrentProject } = useCurrentProject();

  useEffect(() => {
    if (!pathname.startsWith('/projects/') && !pathname.startsWith('/posts/')) {
      clearCurrentProject();
    }
  }, [pathname, clearCurrentProject]);

  return null;
}
