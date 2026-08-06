'use client';

import { useTranslations } from 'next-intl';
import { useParams, useRouter } from 'next/navigation';
import { useState } from 'react';
import { toast } from 'sonner';

import { useUploadMedia } from '@/api/__generated__/media/media';
import {
  getGetMyProjectsQueryKey,
  getGetProjectByHandleQueryOptions,
  getSearchMyProjectsQueryKey,
  useGetProjectByHandle,
  useUpdateProject,
} from '@/api/__generated__/project/project';
import {
  GetProjectResponseRole,
  UpdateProjectRequestJoinPolicy,
} from '@/api/__generated__/types';
import { uploadFileToS3 } from '@/api/s3';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { FileInput, FileInputError, Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Separator } from '@/components/ui/separator';
import SyncError, { ErrorCode } from '@/lib/error';
import ROUTES from '@/util/routes';

const PROJECT_ICON_ALLOWED_TYPES = 'image/*';
const PROJECT_ICON_MAX_SIZE_BYTES = 5 * 1024 * 1024;

const PROJECT_NAME_MIN_LENGTH = 6;
const PROJECT_NAME_MAX_LENGTH = 50;
const PROJECT_HANDLE_MIN_LENGTH = 6;
const PROJECT_HANDLE_MAX_LENGTH = 30;

export default function ProjectSettingsView() {
  const t = useTranslations('pages.projects.project.settings.project');

  const { handle } = useParams<{ handle: string }>();
  const { data, isPending } = useGetProjectByHandle(handle);

  const project = data?.data;
  const isAdmin = project?.role === GetProjectResponseRole.Admin;
  const isMember = project?.role != null;

  if (isPending) {
    return <ProjectSettingsSkeleton />;
  }

  return (
    <div className="space-y-7">
      <div>
        <h2 className="text-lg font-semibold">{t('heading')}</h2>
        <p className="text-sm text-muted-foreground">{t('description')}</p>
      </div>

      <Separator />

      <section className="space-y-4">
        <div className="space-y-1">
          <h3 className="text-sm font-medium">{t('icon.heading')}</h3>
          <p className="text-xs text-muted-foreground">
            {t('icon.description')}
          </p>
        </div>
        <ProjectIconField handle={handle} isAdmin={isAdmin} />
      </section>

      <Separator />

      <section className="space-y-4">
        <div className="space-y-1">
          <h3 className="text-sm font-medium">{t('name.heading')}</h3>
          <p className="text-xs text-muted-foreground">
            {t('name.description')}
          </p>
        </div>
        <ProjectNameField handle={handle} isAdmin={isAdmin} />
      </section>

      <Separator />

      <section className="space-y-4">
        <div className="space-y-1">
          <h3 className="text-sm font-medium">{t('handle.heading')}</h3>
          <p className="text-xs text-muted-foreground">
            {t('handle.description')}
          </p>
        </div>
        <ProjectHandleField handle={handle} isAdmin={isAdmin} />
      </section>

      <Separator />

      <section className="space-y-4">
        <div className="space-y-1">
          <h3 className="text-sm font-medium">{t('join-policy.heading')}</h3>
          <p className="text-xs text-muted-foreground">
            {t('join-policy.description')}
          </p>
        </div>
        <ProjectJoinPolicyField handle={handle} isAdmin={isAdmin} />
      </section>

      <Separator />

      <section className="space-y-4">
        <div className="space-y-1">
          <h3 className="text-sm font-medium text-destructive">
            {t('danger-zone.heading')}
          </h3>
          <p className="text-xs text-muted-foreground">
            {t('danger-zone.description')}
          </p>
        </div>
        <div className="rounded-md border border-destructive/30 divide-y divide-destructive/20">
          <div className="flex items-center justify-between p-4">
            <div>
              <p className="text-sm font-medium">
                {t('danger-zone.leave.title')}
              </p>
              <p className="text-xs text-muted-foreground">
                {t('danger-zone.leave.description')}
              </p>
            </div>
            <Button
              variant="outline"
              size="sm"
              disabled={!isMember}
              className="border-destructive/50 text-destructive hover:bg-destructive/10 disabled:opacity-50"
            >
              {t('danger-zone.leave.action')}
            </Button>
          </div>
          <div className="flex items-center justify-between p-4">
            <div>
              <p className="text-sm font-medium">
                {t('danger-zone.delete.title')}
              </p>
              <p className="text-xs text-muted-foreground">
                {t('danger-zone.delete.description')}
              </p>
            </div>
            <Button variant="destructive" size="sm" disabled={!isAdmin}>
              {t('danger-zone.delete.action')}
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
  const t = useTranslations('pages.projects.project.settings.project.icon');

  const { data } = useGetProjectByHandle(handle);
  const project = data?.data;

  const { mutate: updateProject } = useUpdateProject({
    mutation: {
      onSuccess: async (_data, _variables, _onMutateResult, context) => {
        await Promise.all([
          context.client.invalidateQueries(
            getGetProjectByHandleQueryOptions(handle),
          ),
          context.client.invalidateQueries({
            queryKey: getGetMyProjectsQueryKey(),
          }),
          context.client.invalidateQueries({
            queryKey: getSearchMyProjectsQueryKey(),
          }),
        ]);
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
      setError(t('errors.size'));
    } else if (error === 'type') {
      setError(t('errors.type'));
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
      data: { uploadUrl, mediaId, contentType },
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
      contentType,
    });

    if (!uploadSuccess) {
      toast.error(t('errors.upload-failed'));
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
          toast.success(t('messages.success'));
          setSelectedIcon(null);
        },
        onError: (error) => {
          if (
            error instanceof SyncError &&
            error.code === ErrorCode.NETWORK_ERROR
          ) {
            setError(t('errors.network'));
          } else if (error instanceof SyncError) {
            setError(error.message);
          } else {
            setError(t('errors.upload-failed'));
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
              {project.summary.iconUrl || selectedIcon
                ? t('change')
                : t('upload')}
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
              {t('remove')}
            </Button>
          )}
        </div>

        {error && <p className="text-xs text-destructive">{error}</p>}
      </div>
    </div>
  );
}

function ProjectNameField({
  handle,
  isAdmin,
}: {
  handle: string;
  isAdmin: boolean;
}) {
  const t = useTranslations('pages.projects.project.settings.project.name');

  const { data } = useGetProjectByHandle(handle);
  const project = data?.data;

  const [name, setName] = useState(project?.summary.name ?? '');
  const [error, setError] = useState<string | null>(null);

  const { mutate: updateProject, isPending } = useUpdateProject({
    mutation: {
      onSuccess: async (_data, _variables, _onMutateResult, context) => {
        await Promise.all([
          context.client.invalidateQueries(
            getGetProjectByHandleQueryOptions(handle),
          ),
          context.client.invalidateQueries({
            queryKey: getGetMyProjectsQueryKey(),
          }),
          context.client.invalidateQueries({
            queryKey: getSearchMyProjectsQueryKey(),
          }),
        ]);
        toast.success(t('messages.success'));
      },
      onError: (error) => {
        if (
          error instanceof SyncError &&
          error.code === ErrorCode.NETWORK_ERROR
        ) {
          setError(t('errors.network'));
        } else if (error instanceof SyncError) {
          setError(error.message);
        } else {
          setError(t('errors.unknown'));
        }
      },
    },
  });

  if (!project) {
    return null;
  }

  const trimmedName = name.trim();
  const isValid =
    trimmedName.length >= PROJECT_NAME_MIN_LENGTH &&
    trimmedName.length <= PROJECT_NAME_MAX_LENGTH;
  const isDirty = trimmedName !== project.summary.name;

  const handleSave = () => {
    setError(null);

    if (!isValid) {
      setError(t('errors.invalid'));
      return;
    }

    updateProject({ handle, data: { name: trimmedName } });
  };

  return (
    <>
      <div className="space-y-2">
        <Label htmlFor="project-name">{t('label')}</Label>
        <Input
          id="project-name"
          value={name}
          onChange={(event) => setName(event.target.value)}
          disabled={!isAdmin || isPending}
        />
      </div>
      {error && <p className="text-xs text-destructive">{error}</p>}
      <Button
        size="sm"
        disabled={!isAdmin || !isDirty || isPending}
        onClick={handleSave}
      >
        {t('save')}
      </Button>
    </>
  );
}

function ProjectHandleField({
  handle,
  isAdmin,
}: {
  handle: string;
  isAdmin: boolean;
}) {
  const t = useTranslations('pages.projects.project.settings.project.handle');
  const router = useRouter();

  const { data } = useGetProjectByHandle(handle);
  const project = data?.data;

  const [nextHandle, setNextHandle] = useState(project?.summary.handle ?? '');
  const [error, setError] = useState<string | null>(null);

  const { mutate: updateProject, isPending } = useUpdateProject({
    mutation: {
      onSuccess: async (_data, _variables, _onMutateResult, context) => {
        await Promise.all([
          context.client.invalidateQueries({
            queryKey: getGetMyProjectsQueryKey(),
          }),
          context.client.invalidateQueries({
            queryKey: getSearchMyProjectsQueryKey(),
          }),
        ]);
        toast.success(t('messages.success'));
        router.replace(ROUTES.PROJECT_SETTINGS(nextHandle.trim()));
      },
      onError: (error) => {
        if (
          error instanceof SyncError &&
          error.code === ErrorCode.PROJECT_HANDLE_ALREADY_EXISTS
        ) {
          setError(t('errors.already-exists'));
        } else if (
          error instanceof SyncError &&
          error.code === ErrorCode.NETWORK_ERROR
        ) {
          setError(t('errors.network'));
        } else if (error instanceof SyncError) {
          setError(error.message);
        } else {
          setError(t('errors.unknown'));
        }
      },
    },
  });

  if (!project) {
    return null;
  }

  const trimmedHandle = nextHandle.trim();
  const isValid =
    trimmedHandle.length >= PROJECT_HANDLE_MIN_LENGTH &&
    trimmedHandle.length <= PROJECT_HANDLE_MAX_LENGTH;
  const isDirty = trimmedHandle !== project.summary.handle;

  const handleSave = () => {
    setError(null);

    if (!isValid) {
      setError(t('errors.invalid'));
      return;
    }

    updateProject({ handle, data: { handle: trimmedHandle } });
  };

  return (
    <>
      <div className="space-y-2">
        <Label htmlFor="project-handle">{t('label')}</Label>
        <Input
          id="project-handle"
          value={nextHandle}
          onChange={(event) => setNextHandle(event.target.value)}
          disabled={!isAdmin || isPending}
        />
      </div>
      {error && <p className="text-xs text-destructive">{error}</p>}
      <Button
        size="sm"
        disabled={!isAdmin || !isDirty || isPending}
        onClick={handleSave}
      >
        {t('save')}
      </Button>
    </>
  );
}

function ProjectJoinPolicyField({
  handle,
  isAdmin,
}: {
  handle: string;
  isAdmin: boolean;
}) {
  const t = useTranslations(
    'pages.projects.project.settings.project.join-policy',
  );

  const { data } = useGetProjectByHandle(handle);
  const project = data?.data;

  const { mutate: updateProject, isPending } = useUpdateProject({
    mutation: {
      onSuccess: async (_data, _variables, _onMutateResult, context) => {
        await Promise.all([
          context.client.invalidateQueries(
            getGetProjectByHandleQueryOptions(handle),
          ),
          context.client.invalidateQueries({
            queryKey: getGetMyProjectsQueryKey(),
          }),
          context.client.invalidateQueries({
            queryKey: getSearchMyProjectsQueryKey(),
          }),
        ]);
        toast.success(t('messages.success'));
      },
      onError: () => {
        toast.error(t('messages.error'));
      },
    },
  });

  if (!project) {
    return null;
  }

  const onValueChange = (value: string) => {
    const joinPolicy = value as UpdateProjectRequestJoinPolicy;
    if (joinPolicy === project.summary.joinPolicy) {
      return;
    }

    updateProject({ handle, data: { joinPolicy } });
  };

  if (!project.summary.isPublic) {
    return (
      <div className="max-w-sm space-y-2">
        <Label htmlFor="project-join-policy">{t('label')}</Label>
        <p className="text-sm font-medium">
          {t(`options.${UpdateProjectRequestJoinPolicy.Invite}.label`)}
        </p>
        <p className="text-xs text-muted-foreground">
          {t('private-locked-description')}
        </p>
      </div>
    );
  }

  return (
    <div className="max-w-sm space-y-2">
      <Label htmlFor="project-join-policy">{t('label')}</Label>
      <Select
        value={project.summary.joinPolicy}
        disabled={!isAdmin || isPending}
        onValueChange={onValueChange}
      >
        <SelectTrigger id="project-join-policy" className="w-full">
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          {Object.values(UpdateProjectRequestJoinPolicy).map((policy) => (
            <SelectItem key={policy} value={policy}>
              {t(`options.${policy}.label`)}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
      <p className="text-xs text-muted-foreground">
        {t(`options.${project.summary.joinPolicy}.description`)}
      </p>
    </div>
  );
}

function ProjectSettingsSkeleton() {
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
