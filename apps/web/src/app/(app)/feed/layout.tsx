import { TripleColumnLayout } from '@/components/layout/columns';

interface FeedLayoutProps {
  children: React.ReactNode;
}

export default function FeedLayout({ children }: FeedLayoutProps) {
  return <TripleColumnLayout left={null} right={null} main={children} />;
}
