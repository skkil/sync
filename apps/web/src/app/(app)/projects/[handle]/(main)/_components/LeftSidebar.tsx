'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { GearIcon } from '@phosphor-icons/react';
import { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
import * as z from 'zod';

import {
  getGetProjectByHandleQueryOptions,
  useGetProjectByHandle,
  useUpdateProject,
} from '@/api/__generated__/project/project';
import { GetProjectResponse } from '@/api/__generated__/types';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { Separator } from '@/components/ui/separator';
import { Textarea } from '@/components/ui/textarea';

interface LeftSidebarProps {
  handle: string;
}

export default function LeftSidebar({ handle }: LeftSidebarProps) {
  const { data, isPending } = useGetProjectByHandle(handle);

  if (isPending) {
    return <LeftSidebarSkeleton />;
  }

  if (!data) {
    return null;
  }

  const { data: project } = data;

  return (
    <div className="h-full flex flex-col justify-between">
      <div className="flex flex-col gap-4">
        <AboutCard project={project} />

        <Separator />

        <TeammatesCard project={project} />
      </div>
    </div>
  );
}

// TODO
function LeftSidebarSkeleton() {
  return null;
}

const EditAboutSchema = z.object({
  description: z.string().optional(),
  website: z
    .url({ error: 'Must be a valid URL' })
    .or(z.literal('').transform(() => undefined))
    .optional(),
});

type EditAboutValues = z.infer<typeof EditAboutSchema>;

function EditAboutDialog({
  project,
  children,
}: {
  project: GetProjectResponse;
  children: React.ReactNode;
}) {
  const [open, setOpen] = useState(false);

  const { mutate: updateProject } = useUpdateProject();

  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<EditAboutValues>({
    resolver: zodResolver(EditAboutSchema),
    defaultValues: {
      description: project.description ?? '',
      website: project.website ?? '',
    },
  });

  const onSubmit = async (values: EditAboutValues) => {
    updateProject(
      {
        handle: project.handle,
        data: {
          description: values.description,
          website: values.website,
        },
      },
      {
        onSuccess: (_data, _variables, _onMutateResult, context) => {
          context.client.invalidateQueries(
            getGetProjectByHandleQueryOptions(project.handle),
          );
          toast.success('Updated successfully.');
          setOpen(false);
        },
      },
    );
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Edit About</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
          <FieldGroup>
            <Field>
              <FieldLabel>Description</FieldLabel>
              <Controller
                control={control}
                name="description"
                render={({ field }) => (
                  <Textarea
                    {...field}
                    placeholder="Describe this project..."
                    rows={4}
                  />
                )}
              />
              <FieldError errors={[errors.description]} />
            </Field>
            <Field>
              <FieldLabel>Website</FieldLabel>
              <Controller
                control={control}
                name="website"
                render={({ field }) => (
                  <Input
                    {...field}
                    placeholder="https://example.com"
                    type="url"
                  />
                )}
              />
              <FieldError errors={[errors.website]} />
            </Field>
          </FieldGroup>
          <div className="flex justify-end gap-2">
            <Button
              type="button"
              variant="ghost"
              onClick={() => setOpen(false)}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              Save
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function AboutCard({ project }: { project: GetProjectResponse }) {
  return (
    <div>
      <div className="flex items-center justify-between">
        <span>About</span>
        <div>
          {project.role === 'ADMIN' && (
            <EditAboutDialog project={project}>
              <Button variant="ghost" size="icon">
                <GearIcon />
              </Button>
            </EditAboutDialog>
          )}
        </div>
      </div>

      <div className="flex flex-col gap-1">
        {project.website && (
          <a
            href={project.website}
            target="_blank"
            rel="noopener noreferrer"
            className="text-sm text-blue-500 hover:underline break-all"
          >
            {project.website}
          </a>
        )}
        <div>
          {project.description || (
            <span className="italic">No description provided.</span>
          )}
        </div>
      </div>
    </div>
  );
}

function TeammatesCard({ project }: { project: GetProjectResponse }) {
  return (
    <div>
      <div className="mb-2">Teammates</div>

      <div>
        {project.teammates.map((teammate) => (
          <div key={teammate.handle}>
            <Avatar>
              <AvatarFallback />
            </Avatar>
          </div>
        ))}
        {project.hasMoreTeammates && <span>And more...</span>}
      </div>
    </div>
  );
}
