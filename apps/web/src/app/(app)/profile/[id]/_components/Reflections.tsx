import { PencilIcon, XIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Field, FieldLabel } from '@/components/ui/field';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Textarea } from '@/components/ui/textarea';
import { useCreateReflectionMutation } from '@/features/reflection/api/create-reflection';
import { useGetReflectionsQuery } from '@/features/reflection/api/get-reflections';
import { Experience } from '@/types/experience';

interface ReflectionsProps {
  experience: Experience;
  isOwner: boolean;
  onClose: () => void;
}

export default function Reflections({
  experience,
  isOwner,
  onClose,
}: ReflectionsProps) {
  const t = useTranslations('pages.profile.experience.reflection');

  const { data: reflections } = useGetReflectionsQuery(experience.id);

  return (
    <Card className="w-full">
      <CardHeader>
        <div className="flex items-center justify-between">
          <div>
            <CardTitle>{experience.provider.name}</CardTitle>
            <CardDescription>{t('title')}</CardDescription>
          </div>

          <div className="flex gap-1">
            {isOwner && <AddReflectionDialog experience={experience} />}
            <Button variant="ghost" onClick={onClose}>
              <XIcon />
            </Button>
          </div>
        </div>
      </CardHeader>

      <CardContent>
        <ScrollArea className="h-120">
          {reflections && reflections.length > 0 ? (
            reflections.map((reflection) => (
              <div key={reflection.id} className="mb-4">
                <p className="text-sm">{reflection.content}</p>
                <p className="text-xs text-muted-foreground mt-1">
                  {reflection.createdAt.toDateString()}
                </p>
              </div>
            ))
          ) : (
            <p className="text-sm text-muted-foreground py-4">
              {t('not-found')}
            </p>
          )}
        </ScrollArea>
      </CardContent>
    </Card>
  );
}

function AddReflectionDialog({ experience }: { experience: Experience }) {
  const t = useTranslations(
    'pages.profile.experience.reflection.add-reflection',
  );

  const [content, setContent] = useState('');
  const [open, setOpen] = useState(false);

  const { mutate: createReflection } = useCreateReflectionMutation(
    experience.id,
  );

  const handleClose = () => {
    setContent('');
    setOpen(false);
  };

  return (
    <Dialog open={open} onOpenChange={(open) => setOpen(open)}>
      <DialogTrigger asChild>
        <Button variant="ghost">
          <PencilIcon />
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{t('title')}</DialogTitle>
          <DialogDescription>{t('description')}</DialogDescription>
        </DialogHeader>

        <Field orientation="vertical">
          <FieldLabel>{t('form.content.label')}</FieldLabel>
          <Textarea
            rows={10}
            value={content}
            placeholder={t('form.content.placeholder')}
            onChange={(e) => setContent(e.target.value)}
          />
        </Field>

        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => {
              handleClose();
            }}
          >
            {t('form.cancel.label')}
          </Button>
          <Button
            onClick={() => {
              createReflection(
                { content },
                {
                  onSuccess: () => {
                    handleClose();
                  },
                },
              );
            }}
          >
            {t('form.submit.label')}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
