import { useEffect, useState } from 'react';

/**
 * Tracks whether the component has mounted on the client. SSR always renders
 * `false`; gating client-only subtrees on this keeps the first client render
 * identical to the server-rendered HTML so it doesn't diverge and trigger a
 * hydration mismatch.
 */
export function useMounted() {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect -- intentional hydration gate: this is the one legitimate case for setting state in an effect with no deps
    setMounted(true);
  }, []);

  return mounted;
}
