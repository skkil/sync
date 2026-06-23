'use client';

import {
  ChatCircleIcon,
  DotsThreeIcon,
  HeartIcon,
  SirenIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useState } from 'react';
import { toast } from 'sonner';

import { useReportPost } from '@/api/__generated__/post-report/post-report';
import { useGetPostBySlug } from '@/api/__generated__/post/post';
import { ReportPostRequestReason } from '@/api/__generated__/types/ReportPostRequestReason';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardAction,
  CardContent,
  CardFooter,
  CardHeader,
} from '@/components/ui/card';
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { BaseViewer } from '@/components/ui/editor';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Skeleton } from '@/components/ui/skeleton';
import { Textarea } from '@/components/ui/textarea';
import PostBookmarkButton from '@/features/bookmark/components/PostBookmarkButton';
import { useSession } from '@/lib/auth/client';

interface PostContentProps {
  slug: string;
}

export default function PostContent({ slug }: PostContentProps) {
  const t = useTranslations('pages.posts.report');
  const { data: session } = useSession();
  const isAuthenticated = session === undefined ? undefined : !!session?.user;
  const { data, isPending } = useGetPostBySlug(slug);
  const { mutate: reportPost, isPending: isReportPending } = useReportPost();
  const [reportOpen, setReportOpen] = useState(false);
  const [reason, setReason] = useState<ReportPostRequestReason>(
    ReportPostRequestReason.Spam,
  );
  const [description, setDescription] = useState('');

  if (isPending) {
    return <Skeleton className="h-96 w-full" />;
  }

  if (!data) {
    return null;
  }

  const post = data.data;

  const submitReport = () => {
    if (!isAuthenticated) {
      toast.error(t('messages.login-required'));
      return;
    }

    reportPost(
      {
        postId: String(post.id),
        data: {
          reason,
          description: description.trim() || null,
        },
      },
      {
        onSuccess: () => {
          toast.success(t('messages.success'));
          setDescription('');
          setReason(ReportPostRequestReason.Spam);
          setReportOpen(false);
        },
        onError: () => {
          toast.error(t('messages.error'));
        },
      },
    );
  };

  return (
    <>
      <Card>
        <CardHeader>
          <div className="flex items-start justify-between">
            <div className="flex items-center space-x-3">
              <Avatar size="lg">
                <AvatarFallback />
              </Avatar>
              <div className="flex flex-col gap-2">{post.author.name}</div>
            </div>

            <CardAction>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" size="icon-sm">
                    <DotsThreeIcon weight="bold" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem
                    variant="destructive"
                    onSelect={(event) => {
                      event.preventDefault();
                      setReportOpen(true);
                    }}
                  >
                    <SirenIcon />
                    {t('trigger')}
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </CardAction>
          </div>
        </CardHeader>

        <CardContent className="space-y-6">
          <div className="space-y-3">
            <BaseViewer content={post.content} />
          </div>
        </CardContent>

        <CardFooter className="justify-between">
          <div className="flex items-center gap-2">
            <Button variant="ghost" size="sm" className="gap-1.5">
              <HeartIcon />
              {post.likeCount}
            </Button>

            <Button variant="ghost" size="sm" className="gap-1.5">
              <ChatCircleIcon />
              {post.commentCount}
            </Button>
          </div>

          <PostBookmarkButton
            postId={post.id}
            slug={post.slug}
            bookmarked={post.bookmarked}
            isAuthenticated={isAuthenticated}
            size="sm"
          />
        </CardFooter>
      </Card>

      <Dialog open={reportOpen} onOpenChange={setReportOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{t('title')}</DialogTitle>
          </DialogHeader>

          <div className="space-y-4">
            <div className="space-y-2">
              <Label>{t('fields.reason')}</Label>
              <Select
                value={reason}
                onValueChange={(value) =>
                  setReason(value as ReportPostRequestReason)
                }
              >
                <SelectTrigger className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {Object.values(ReportPostRequestReason).map((value) => (
                    <SelectItem key={value} value={value}>
                      {t(`reasons.${value}`)}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label>{t('fields.description')}</Label>
              <Textarea
                value={description}
                maxLength={1000}
                onChange={(event) => setDescription(event.target.value)}
                placeholder={t('fields.description-placeholder')}
              />
            </div>
          </div>

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => setReportOpen(false)}
            >
              {t('actions.cancel')}
            </Button>
            <Button
              type="button"
              variant="destructive"
              isPending={isReportPending}
              onClick={submitReport}
            >
              {t('actions.submit')}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
