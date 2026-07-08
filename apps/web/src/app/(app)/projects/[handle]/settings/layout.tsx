import { requireOnboardedSession } from '@/lib/auth/guards';

import SettingsTabs from './_components/SettingsTabs';

interface ProjectSettingsLayoutProps {
  children: React.ReactNode;
}

export default async function ProjectSettingsLayout({
  children,
}: ProjectSettingsLayoutProps) {
  await requireOnboardedSession();

  return <SettingsTabs>{children}</SettingsTabs>;
}
