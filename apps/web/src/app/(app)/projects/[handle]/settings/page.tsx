import { redirect } from 'next/navigation';

interface ProjectSettingsPageProps {
  params: Promise<{
    handle: string;
  }>;
}

export default async function ProjectSettingsPage({
  params,
}: ProjectSettingsPageProps) {
  const { handle } = await params;
  redirect(`/projects/${handle}/settings/teammates`);
}
