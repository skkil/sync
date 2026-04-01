import CreateTeamBuildingPostForm from './_components/CreateTeamBuildingPostForm';
import TeamBuildingPostList from './_components/TeamBuildingPostList';

interface ProjectTeamBuildingProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function ProjectTeamBuilding({
  params,
}: ProjectTeamBuildingProps) {
  const { id } = await params;

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <CreateTeamBuildingPostForm projectId={id} />
      </div>

      <TeamBuildingPostList projectId={id} />
    </div>
  );
}
