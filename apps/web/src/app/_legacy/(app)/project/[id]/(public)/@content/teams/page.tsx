import TeamBuildingPostList from './_components/TeamBuildingPostList';

interface ProjectTeamBuildingPostsProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function ProjectTeamBuildingPosts({
  params,
}: ProjectTeamBuildingPostsProps) {
  const { id } = await params;

  return <TeamBuildingPostList projectId={id} />;
}
