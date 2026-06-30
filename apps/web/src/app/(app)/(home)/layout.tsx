import { TripleColumnLayout } from '@/components/layout/columns';

interface HomeLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: HomeLayoutProps) {
  return <TripleColumnLayout left={null} right={null} main={children} />;
}
