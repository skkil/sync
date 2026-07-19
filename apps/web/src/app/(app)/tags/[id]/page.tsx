import { Metadata } from 'next';

import { getTag } from '@/api/__generated__/tag/tag';
import TagDetailHeader from '@/components/feature/tag/detail/TagDetailHeader';
import TagPostFeed from '@/components/feature/tag/detail/TagPostFeed';

interface TagDetailPageProps {
  params: Promise<{
    id: string;
  }>;
}

export async function generateMetadata({
  params,
}: TagDetailPageProps): Promise<Metadata> {
  const { id } = await params;

  try {
    const { data } = await getTag(id);

    return { title: data.tag?.name };
  } catch {
    return {};
  }
}

export default async function TagDetailPage({ params }: TagDetailPageProps) {
  const { id } = await params;

  return (
    <div className="space-y-6">
      <TagDetailHeader tagId={id} />
      <TagPostFeed tagId={id} />
    </div>
  );
}
