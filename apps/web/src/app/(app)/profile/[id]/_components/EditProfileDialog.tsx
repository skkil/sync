import { zodResolver } from '@hookform/resolvers/zod';
import { PencilIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useEffect, useMemo, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
import z from 'zod';

import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
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
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Textarea } from '@/components/ui/textarea';
import { useGetProfileQuery } from '@/features/profile/api/get-profile';
import { useUpdateProfileMutation } from '@/features/profile/api/update-profile';
import { ContactFields } from '@/features/profile/util/contacts';
import { useSession } from '@/lib/auth/client';

const EditProfileFormSchema = (t: ReturnType<typeof useTranslations>) =>
  z.object({
    name: z.string().min(1, t('form.errors.required_name')),
    profession: z.string(),
    bio: z.string(),
    contacts: z.object({
      custom: z.string(),
      linkedin: z.string(),
      github: z.string(),
      instagram: z.string(),
      twitter: z.string(),
    }),
  });

export default function EditProfileDialog() {
  const t = useTranslations('pages.profile.edit');

  const { data: session, refetch: refetchSession } = useSession();
  const { data: profile } = useGetProfileQuery(session?.user.id || '');

  const { mutate: updateProfile, isPending: isUpdateProfilePending } =
    useUpdateProfileMutation();

  const schema = useMemo(() => EditProfileFormSchema(t), [t]);
  type EditProfileFormValues = z.infer<typeof schema>;

  const form = useForm<EditProfileFormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: '',
      profession: '',
      bio: '',
      contacts: {
        custom: '',
        linkedin: '',
        github: '',
        instagram: '',
        twitter: '',
      },
    },
  });

  const [open, setOpen] = useState(false);

  useEffect(() => {
    if (profile) {
      form.reset({
        name: profile.name || '',
        profession: profile.profession || '',
        bio: profile.bio || '',
        contacts: {
          custom: profile.contacts?.custom || '',
          linkedin: profile.contacts?.linkedin || '',
          github: profile.contacts?.github || '',
          instagram: profile.contacts?.instagram || '',
          twitter: profile.contacts?.twitter || '',
        },
      });
    }
  }, [form, profile]);

  const formSubmitHandler = (values: EditProfileFormValues) => {
    updateProfile(values, {
      onSuccess: (_data, _variables, _result, context) => {
        setOpen(false);
        context.client.invalidateQueries({
          queryKey: ['profile', session?.user.id],
        });
        refetchSession();
      },
      onError: () => {
        toast.error(t('form.errors.invalid_fields'));
      },
    });
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="ghost" aria-label={t('title')}>
          <PencilIcon />
        </Button>
      </DialogTrigger>

      <DialogContent className="min-w-5xl">
        <DialogHeader>
          <div className="flex items-center justify-between">
            <div className="flex flex-col gap-2">
              <DialogTitle>{t('title')}</DialogTitle>
              <DialogDescription>{t('description')}</DialogDescription>
            </div>

            <DialogClose />
          </div>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(formSubmitHandler)}>
          <ScrollArea className="min-h-120">
            <h2 className="text-lg font-bold">{t('form.groups.basic')}</h2>
            <FieldGroup className="p-3">
              <Controller
                name="name"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <div className="flex items-center justify-between">
                      <FieldLabel>{t('form.fields.name.label')}</FieldLabel>

                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </div>

                    <Input
                      {...field}
                      aria-invalid={fieldState.invalid}
                      placeholder={t('form.fields.name.placeholder')}
                      autoComplete="name"
                    />
                  </Field>
                )}
              />

              <Controller
                name="profession"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <div className="flex items-center justify-between">
                      <FieldLabel>
                        {t('form.fields.profession.label')}
                      </FieldLabel>

                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </div>

                    <Input
                      {...field}
                      aria-invalid={fieldState.invalid}
                      placeholder={t('form.fields.profession.placeholder')}
                    />
                  </Field>
                )}
              />

              <Controller
                name="bio"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <div className="flex items-center justify-between">
                      <FieldLabel>{t('form.fields.bio.label')}</FieldLabel>

                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </div>

                    <Textarea
                      {...field}
                      aria-invalid={fieldState.invalid}
                      placeholder={t('form.fields.bio.placeholder')}
                    />
                  </Field>
                )}
              />
            </FieldGroup>

            <h2 className="text-lg font-bold">{t('form.groups.contacts')}</h2>
            <FieldGroup className="p-3">
              {ContactFields.map((contactField) => (
                <Controller
                  key={contactField.id}
                  name={`contacts.${contactField.id}`}
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <InputGroup>
                        <InputGroupAddon>{contactField.icon}</InputGroupAddon>
                        {contactField.prefix && (
                          <InputGroupAddon className="text-muted-foreground">
                            {contactField.prefix}
                          </InputGroupAddon>
                        )}
                        <InputGroupInput {...field} />
                      </InputGroup>
                    </Field>
                  )}
                />
              ))}
            </FieldGroup>
          </ScrollArea>

          <DialogFooter>
            <Button isPending={isUpdateProfilePending} type="submit">
              {t('form.actions.submit.label')}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
