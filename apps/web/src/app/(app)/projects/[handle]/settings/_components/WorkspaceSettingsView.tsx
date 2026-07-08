'use client';

import { useParams } from 'next/navigation';
import { useState } from 'react';
import { toast } from 'sonner';

import { useUploadMedia } from '@/api/__generated__/media/media';
import {
  getGetProjectByHandleQueryOptions,
  useGetProjectByHandle,
  useUpdateProject,
} from '@/api/__generated__/project/project';
import { GetProjectResponseRole } from '@/api/__generated__/types';
import { uploadFileToS3 } from '@/api/s3';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { FileInput, FileInputError, Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Separator } from '@/components/ui/separator';
import SyncError, { ErrorCode } from '@/lib/error';

const PROJECT_ICON_ALLOWED_TYPES = 'image/*';
const PROJECT_ICON_MAX_SIZE_BYTES = 5 * 1024 * 1024;

export default function WorkspaceSettingsView() {
  const { handle } = useParams<{ handle: string }>();
  const { data, isPending } = useGetProjectByHandle(handle);

  const project = data?.data;
  const isAdmin = project?.role === GetProjectResponseRole.Admin;
  const isMember = project?.role != null;

  if (isPending) {
    return <WorkspaceSettingsSkeleton />;
  }

  return (
    <div className="space-y-7">
      <div>
        <h2 className="text-lg font-semibold">워크스페이스 설정</h2>
        <p className="text-sm text-muted-foreground">
          프로젝트의 기본 정보를 관리합니다.
        </p>
      </div>

      <Separator />

      <section className="space-y-4">
        <div className="space-y-1">
          <h3 className="text-sm font-medium">프로젝트 아이콘</h3>
          <p className="text-xs text-muted-foreground">
            프로젝트를 대표하는 아이콘 이미지입니다.
          </p>
        </div>
        <ProjectIconField handle={handle} isAdmin={isAdmin} />
      </section>

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
            defaultValue={project?.summary.name ?? ''}
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
            defaultValue={project?.summary.handle ?? ''}
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

function ProjectIconField({
  handle,
  isAdmin,
}: {
  handle: string;
  isAdmin: boolean;
}) {
  const { data } = useGetProjectByHandle(handle);
  const project = data?.data;

  const { mutate: updateProject } = useUpdateProject({
    mutation: {
      onSuccess: async (_data, _variables, _onMutateResult, context) => {
        await context.client.invalidateQueries(
          getGetProjectByHandleQueryOptions(handle),
        );
      },
    },
  });

  const { mutateAsync: uploadMedia, isPending: isUploadMediaPending } =
    useUploadMedia();

  const [selectedIcon, setSelectedIcon] = useState<{
    file: File;
    src: string;
  } | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleFileError = (error: FileInputError) => {
    if (error === 'size') {
      setError('파일 크기는 5MB 이하여야 합니다.');
    } else if (error === 'type') {
      setError('지원하지 않는 파일 형식입니다.');
    }
  };

  const handleFileChange = async (files: File[]) => {
    const file = files[0];
    if (!file) {
      return;
    }

    setSelectedIcon({
      file,
      src: URL.createObjectURL(file),
    });
    setError(null);

    const {
      data: { uploadUrl, mediaId },
    } = await uploadMedia({
      data: {
        fileName: file.name,
        fileSize: file.size,
        mediaType: file.type,
      },
    });

    const { success: uploadSuccess } = await uploadFileToS3({
      file,
      uploadUrl,
    });

    if (!uploadSuccess) {
      toast.error('업로드에 실패했습니다.');
      setSelectedIcon(null);
      return;
    }

    updateProject(
      {
        handle,
        data: {
          iconMediaId: mediaId,
        },
      },
      {
        onSuccess: () => {
          toast.success('프로젝트 아이콘이 변경되었습니다.');
          setSelectedIcon(null);
        },
        onError: (error) => {
          if (
            error instanceof SyncError &&
            error.code === ErrorCode.NETWORK_ERROR
          ) {
            setError('서버에 연결할 수 없습니다. 나중에 다시 시도하세요.');
          } else if (error instanceof SyncError) {
            setError(error.message);
          } else {
            setError('업로드에 실패했습니다.');
          }
        },
      },
    );
  };

  const handleRemoveIcon = () => {
    setSelectedIcon(null);
    updateProject({
      handle,
      data: {
        removeIcon: true,
      },
    });
  };

  if (!project) {
    return null;
  }

  return (
    <div className="flex gap-4">
      <Avatar className="h-16 w-16 rounded-lg">
        <AvatarImage
          src={
            selectedIcon
              ? selectedIcon.src
              : project.summary.iconUrl || undefined
          }
        />
        <AvatarFallback className="rounded-lg">
          {project.summary.name.charAt(0).toUpperCase()}
        </AvatarFallback>
      </Avatar>

      <div className="flex flex-col gap-2">
        <div className="flex gap-2">
          <FileInput
            onFileChange={handleFileChange}
            onError={handleFileError}
            accept={PROJECT_ICON_ALLOWED_TYPES}
            maxSize={PROJECT_ICON_MAX_SIZE_BYTES}
            disabled={!isAdmin || isUploadMediaPending}
          >
            <Button type="button" size="sm" disabled={!isAdmin}>
              {project.summary.iconUrl || selectedIcon ? '변경' : '업로드'}
            </Button>
          </FileInput>

          {(project.summary.iconUrl || selectedIcon) && (
            <Button
              type="button"
              variant="destructive"
              size="sm"
              disabled={!isAdmin}
              onClick={() => handleRemoveIcon()}
            >
              제거
            </Button>
          )}
        </div>

        {error && <p className="text-xs text-destructive">{error}</p>}
      </div>
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
