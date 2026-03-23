import { PencilIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { useCreateExperienceMutation } from '@/features/experience/api/create-experience';
import { useSession } from '@/lib/auth/client';
import { ExperienceType } from '@/types/experience';

import ExperienceForm, { ExperienceFormValues } from './ExperienceForm';

interface AddExperienceButtonProps {
  type: ExperienceType;
}

export default function AddExperienceButton({
  type,
}: AddExperienceButtonProps) {
  const t = useTranslations('pages.profile.experience.add-experience');

  const { data: session } = useSession();

  const [open, setOpen] = useState(false);

  const { mutate: createExperience } = useCreateExperienceMutation(
    session?.user.id || '',
  );

  const handleSubmit = (values: ExperienceFormValues) => {
    createExperience(
      {
        ...values,
        providerId: values.provider.id,
        startDate: new Date(),
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
        <Button variant="ghost" size="icon">
          <PencilIcon />
        </Button>
      </DialogTrigger>

      <DialogContent>
        <DialogHeader>
          <DialogTitle>{t(`title.${type}`)}</DialogTitle>
          <DialogDescription>{t(`description.${type}`)}</DialogDescription>
        </DialogHeader>

        <ExperienceForm
          type={type}
          onSubmit={handleSubmit}
          onClose={() => setOpen(false)}
        />
      </DialogContent>
    </Dialog>
  );
}
