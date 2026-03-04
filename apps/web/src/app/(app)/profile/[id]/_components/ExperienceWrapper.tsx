'use client';

import {
  BookIcon,
  DotsThreeIcon,
  EyeClosedIcon,
  EyeIcon,
  PencilIcon,
  TrashIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useState } from 'react';

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuPortal,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuSub,
  DropdownMenuSubContent,
  DropdownMenuSubTrigger,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { useDeleteExperienceMutation } from '@/features/experience/api/delete-experience';
import { useUpdateExperienceMutation } from '@/features/experience/api/update-experience';
import { useSession } from '@/lib/auth/client';
import { Experience, ExperienceVisibility } from '@/types/experience';

import ExperienceForm, { ExperienceFormValues } from './ExperienceForm';
import Reflections from './Reflections';

interface ExperienceWrapperProps {
  userId: string;
  experience: Experience;
  children: React.ReactNode;
}

export default function ExperienceWrapper({
  userId,
  experience,
  children,
}: ExperienceWrapperProps) {
  const t = useTranslations('pages.profile.experience');

  const { data: session } = useSession();

  const [showReflections, setShowReflections] = useState(false);

  const { mutate: updateExperience } = useUpdateExperienceMutation(
    session?.user.id || '',
  );

  const isOwner = session?.user.id === userId;

  return (
    <div className="flex justify-between items-center">
      {showReflections ? (
        <Reflections
          experience={experience}
          isOwner={isOwner}
          onClose={() => {
            setShowReflections(false);
          }}
        />
      ) : (
        <>
          <div>{children}</div>

          <div className="flex items-center gap-2">
            <Button
              variant="ghost"
              onClick={() => setShowReflections((prev) => !prev)}
            >
              <BookIcon />
            </Button>

            {isOwner && (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost">
                    <DotsThreeIcon />
                  </Button>
                </DropdownMenuTrigger>

                <DropdownMenuContent>
                  <UpdateExperienceButton experience={experience} />

                  <DropdownMenuSub>
                    <DropdownMenuSubTrigger>
                      <EyeIcon />
                      {t('menu.visibility.title')}
                    </DropdownMenuSubTrigger>
                    <DropdownMenuPortal>
                      <DropdownMenuSubContent>
                        <DropdownMenuGroup>
                          <DropdownMenuLabel>
                            {t('menu.visibility.title')}
                          </DropdownMenuLabel>
                          <DropdownMenuRadioGroup
                            value={experience.visibility}
                            onValueChange={(visibility) => {
                              updateExperience({
                                experienceId: experience.id,
                                data: {
                                  type: experience.type,
                                  visibility:
                                    visibility as ExperienceVisibility,
                                },
                              });
                            }}
                          >
                            <DropdownMenuRadioItem value="PUBLIC">
                              <EyeIcon />
                              {t('menu.visibility.options.PUBLIC')}
                            </DropdownMenuRadioItem>
                            <DropdownMenuRadioItem value="PRIVATE">
                              <EyeClosedIcon />
                              {t('menu.visibility.options.PRIVATE')}
                            </DropdownMenuRadioItem>
                          </DropdownMenuRadioGroup>
                        </DropdownMenuGroup>
                      </DropdownMenuSubContent>
                    </DropdownMenuPortal>
                  </DropdownMenuSub>

                  <DeleteExperienceButton experience={experience} />
                </DropdownMenuContent>
              </DropdownMenu>
            )}
          </div>
        </>
      )}
    </div>
  );
}

function UpdateExperienceButton({ experience }: { experience: Experience }) {
  const t = useTranslations('pages.profile.experience.update-experience');

  const { data: session } = useSession();

  const [open, setOpen] = useState(false);

  const { mutate: updateExperience } = useUpdateExperienceMutation(
    session?.user.id || '',
  );

  const formSubmitHandler = (values: ExperienceFormValues) => {
    updateExperience(
      {
        experienceId: experience.id,
        data: {
          ...values,
        },
      },
      {
        onSuccess: () => {
          setOpen(false);
        },
      },
    );
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <DropdownMenuItem
          onSelect={(e) => {
            e.preventDefault();
          }}
        >
          <PencilIcon />
          {t('menu')}
        </DropdownMenuItem>
      </DialogTrigger>

      <DialogContent>
        <DialogHeader>
          <DialogTitle>{t('title')}</DialogTitle>
          <DialogDescription>{t('description')}</DialogDescription>
        </DialogHeader>

        <ExperienceForm
          type={experience.type}
          experience={experience}
          onSubmit={formSubmitHandler}
          onClose={() => setOpen(false)}
        />
      </DialogContent>
    </Dialog>
  );
}

function DeleteExperienceButton({ experience }: { experience: Experience }) {
  const t = useTranslations('pages.profile.experience.delete-experience');

  const { data: session } = useSession();

  const [open, setOpen] = useState(false);

  const { mutate: deleteExperience } = useDeleteExperienceMutation({
    userId: session?.user.id || '',
  });

  const onConfirmDelete = () => {
    deleteExperience(experience.id);
  };

  return (
    <AlertDialog open={open} onOpenChange={setOpen}>
      <AlertDialogTrigger asChild>
        <DropdownMenuItem
          onSelect={(e) => {
            e.preventDefault();
          }}
        >
          <TrashIcon />
          {t('menu')}
        </DropdownMenuItem>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{t('title')}</AlertDialogTitle>
          <AlertDialogDescription>{t('description')}</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>{t('actions.cancel')}</AlertDialogCancel>
          <AlertDialogAction variant="destructive" onClick={onConfirmDelete}>
            {t('actions.confirm')}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
