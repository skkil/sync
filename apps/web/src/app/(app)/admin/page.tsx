import { redirect } from 'next/navigation';

import ROUTES from '@/util/routes';

export default function AdminPage() {
  redirect(ROUTES.ADMIN_POST_REPORTS());
}
