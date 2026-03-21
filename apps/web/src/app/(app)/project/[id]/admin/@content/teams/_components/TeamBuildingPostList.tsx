'use client';

import useGetProjectTeamBuildingPostsQuery from '@/features/provider/api/project/get-project-team-building-posts';

interface TeamBuildingPostListProps {
  projectId: string;
}

export default function TeamBuildingPostList({
  projectId,
}: TeamBuildingPostListProps) {
  const { data: posts } = useGetProjectTeamBuildingPostsQuery(projectId);

  if (!posts) {
    return null;
  }

  return (
    <div>
      {posts.map((post) => (
        <div key={post.id}>{post.title}</div>
      ))}
    </div>
  );
}
