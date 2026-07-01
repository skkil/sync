'use client';

import { useParams } from 'next/navigation';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import { GetProjectResponseRole } from '@/api/__generated__/types';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Separator } from '@/components/ui/separator';

export default function WorkspaceSettingsPage() {
  const { handle } = useParams<{ handle: string }>();
  const { data, isPending } = useGetProjectByHandle(handle);

  const project = data?.data;
  const isAdmin = project?.role === GetProjectResponseRole.Admin;
  const isMember = project?.role != null;

  if (isPending) {
    return <WorkspaceSettingsSkeleton />;
  }

  return (
    <div className="max-w-2xl space-y-10">
      <div>
        <h2 className="text-lg font-semibold">워크스페이스 설정</h2>
        <p className="text-sm text-muted-foreground">
          프로젝트의 기본 정보를 관리합니다.
        </p>
      </div>

      <Separator />

      <section className="space-y-4">
        <div className="space-y-1">
          <h3 className="text-sm font-medium">프로젝트 이름</h3>
          <p className="text-xs text-muted-foreground">
            프로젝트의 표시 이름입니다.
          </p>
        </div>
        <div className="space-y-2">
          <Label htmlFor="project-name">이름</Label>
          <Input
            id="project-name"
            defaultValue={project?.name ?? ''}
            disabled={!isAdmin}
          />
        </div>
        <Button disabled={!isAdmin} size="sm">
          저장
        </Button>
      </section>

      <Separator />

      <section className="space-y-4">
        <div className="space-y-1">
          <h3 className="text-sm font-medium">URL 슬러그</h3>
          <p className="text-xs text-muted-foreground">
            프로젝트 URL에 사용되는 고유 식별자입니다.
          </p>
        </div>
        <div className="space-y-2">
          <Label htmlFor="project-handle">슬러그</Label>
          <Input
            id="project-handle"
            defaultValue={project?.handle ?? ''}
            disabled={!isAdmin}
          />
        </div>
        <Button disabled={!isAdmin} size="sm">
          저장
        </Button>
      </section>

      <Separator />

      <section className="space-y-4">
        <div className="space-y-1">
          <h3 className="text-sm font-medium text-destructive">위험 구역</h3>
          <p className="text-xs text-muted-foreground">
            이 작업은 되돌릴 수 없습니다. 신중하게 진행하세요.
          </p>
        </div>
        <div className="rounded-md border border-destructive/30 divide-y divide-destructive/20">
          <div className="flex items-center justify-between p-4">
            <div>
              <p className="text-sm font-medium">워크스페이스 나가기</p>
              <p className="text-xs text-muted-foreground">
                이 프로젝트에서 탈퇴합니다.
              </p>
            </div>
            <Button
              variant="outline"
              size="sm"
              disabled={!isMember}
              className="border-destructive/50 text-destructive hover:bg-destructive/10 disabled:opacity-50"
            >
              나가기
            </Button>
          </div>
          <div className="flex items-center justify-between p-4">
            <div>
              <p className="text-sm font-medium">워크스페이스 삭제</p>
              <p className="text-xs text-muted-foreground">
                프로젝트와 모든 데이터를 영구적으로 삭제합니다.
              </p>
            </div>
            <Button variant="destructive" size="sm" disabled={!isAdmin}>
              삭제
            </Button>
          </div>
        </div>
      </section>
    </div>
  );
}

function WorkspaceSettingsSkeleton() {
  return (
    <div className="max-w-2xl space-y-6">
      <div className="space-y-2">
        <div className="h-5 w-40 rounded bg-muted animate-pulse" />
        <div className="h-4 w-64 rounded bg-muted animate-pulse" />
      </div>
      <Separator />
      <div className="space-y-3">
        <div className="h-4 w-24 rounded bg-muted animate-pulse" />
        <div className="h-9 w-full rounded bg-muted animate-pulse" />
        <div className="h-8 w-16 rounded bg-muted animate-pulse" />
      </div>
    </div>
  );
}
