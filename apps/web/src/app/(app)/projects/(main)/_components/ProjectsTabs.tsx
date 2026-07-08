'use client';

import { usePathname, useRouter, useSearchParams } from 'next/navigation';

import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

import FollowedProjects from './FollowedProjects';
import ProjectInvitations from './ProjectInvitations';
import UserProjects from './UserProjects';

const TABS = ['member', 'following', 'invitations'] as const;
type Tab = (typeof TABS)[number];

function isTab(value: string | undefined): value is Tab {
  return TABS.includes(value as Tab);
}

export default function ProjectsTabs() {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const tabParam = searchParams.get('tab') ?? undefined;
  const activeTab: Tab = isTab(tabParam) ? tabParam : 'member';

  const handleTabChange = (value: string) => {
    if (!isTab(value)) {
      return;
    }

    const params = new URLSearchParams();
    params.set('tab', value);
    router.push(`${pathname}?${params.toString()}`);
  };

  return (
    <Tabs value={activeTab} onValueChange={handleTabChange}>
      <TabsList variant="line" className="border-b w-full justify-start">
        <TabsTrigger value="member">Member</TabsTrigger>
        <TabsTrigger value="following">Following</TabsTrigger>
        <TabsTrigger value="invitations">Invitations</TabsTrigger>
      </TabsList>

      <TabsContent value="member">
        <UserProjects />
      </TabsContent>
      <TabsContent value="following">
        <FollowedProjects />
      </TabsContent>
      <TabsContent value="invitations">
        <ProjectInvitations />
      </TabsContent>
    </Tabs>
  );
}
