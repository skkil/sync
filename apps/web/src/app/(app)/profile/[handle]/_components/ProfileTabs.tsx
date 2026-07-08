'use client';

import { useTranslations } from 'next-intl';

import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useSession } from '@/lib/auth/client';

import ProfileLikes from './ProfileLikes';
import ProfilePosts from './ProfilePosts';
import ProfileQuestions from './ProfileQuestions';

interface ProfileTabsProps {
  handle: string;
}

export default function ProfileTabs({ handle }: ProfileTabsProps) {
  const t = useTranslations('pages.profile');

  const { data: session } = useSession();
  const { data: profile } = useGetProfileByHandle(handle);

  const isOwnProfile =
    !!session?.user.id &&
    !!profile?.data.userId &&
    String(session.user.id) === String(profile.data.userId);

  return (
    <Tabs defaultValue="posts">
      <TabsList variant="line">
        <TabsTrigger value="posts">{t('posts.label')}</TabsTrigger>
        <TabsTrigger value="questions">{t('tabs.questions.label')}</TabsTrigger>
        {isOwnProfile && (
          <TabsTrigger value="likes">{t('tabs.likes.label')}</TabsTrigger>
        )}
      </TabsList>

      <TabsContent value="posts">
        <ProfilePosts handle={handle} />
      </TabsContent>

      <TabsContent value="questions">
        <ProfileQuestions handle={handle} />
      </TabsContent>

      {isOwnProfile && (
        <TabsContent value="likes">
          <ProfileLikes />
        </TabsContent>
      )}
    </Tabs>
  );
}
