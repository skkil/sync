'use client';

import { CompassIcon } from '@phosphor-icons/react';

export default function ExplorePage() {
  return (
    <div className="mx-auto flex max-w-2xl flex-col items-center gap-3 px-4 py-24 text-center">
      <CompassIcon className="text-muted-foreground size-10" />
      <p className="text-muted-foreground text-sm">Nothing to explore yet.</p>
    </div>
  );
}
