import { inferAdditionalFields } from 'better-auth/client/plugins';
import { createAuthClient } from 'better-auth/react';

import type { auth } from '.';

export const { useSession, signOut } = createAuthClient({
  plugins: [inferAdditionalFields<typeof auth>()],
});
