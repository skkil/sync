import TagPosts from './_components/TagPosts';

interface TagPageProps {
  params: Promise<{
    name: string;
  }>;
}

function decodeTagName(value: string) {
  try {
    return decodeURIComponent(value);
  } catch {
    return value;
  }
}

export default async function TagPage({ params }: TagPageProps) {
  const { name } = await params;

  return <TagPosts name={decodeTagName(name)} />;
}
