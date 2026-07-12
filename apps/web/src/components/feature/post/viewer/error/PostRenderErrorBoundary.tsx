'use client';

import { WarningCircleIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { Component, type ReactNode } from 'react';

import { Card, CardContent } from '@/components/ui/card';

interface PostRenderErrorBoundaryProps {
  children: ReactNode;
}

interface PostRenderErrorBoundaryState {
  hasError: boolean;
}

export default class PostRenderErrorBoundary extends Component<
  PostRenderErrorBoundaryProps,
  PostRenderErrorBoundaryState
> {
  constructor(props: PostRenderErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  override componentDidCatch(error: unknown) {
    console.error('Failed to render post', error);
  }

  override render() {
    if (this.state.hasError) {
      return <PostRenderErrorFallback />;
    }

    return this.props.children;
  }
}

function PostRenderErrorFallback() {
  const t = useTranslations('pages.posts.render-error');

  return (
    <Card>
      <CardContent className="flex flex-col items-center gap-2 py-8 text-center">
        <WarningCircleIcon className="text-muted-foreground" size={24} />
        <p className="text-sm font-medium">{t('title')}</p>
        <p className="text-sm text-muted-foreground">{t('description')}</p>
      </CardContent>
    </Card>
  );
}
