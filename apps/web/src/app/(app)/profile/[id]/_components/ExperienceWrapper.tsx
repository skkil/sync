import {
  DotsThreeIcon,
  EyeClosedIcon,
  EyeIcon,
  TrashIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

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
import { useUpdateExperienceMutation } from '@/features/experience/api/update-experience';
import { useSession } from '@/lib/auth/client';
import { Experience, ExperienceVisibility } from '@/types/experience';

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

  const { mutate: updateExperience } = useUpdateExperienceMutation(
    session?.user.id || '',
  );

  const isOwner = session?.user.id === userId;

  return (
    <div className="flex justify-between items-center">
      <div>{children}</div>
      {isOwner && (
        <DropdownMenu>
          <DropdownMenuTrigger>
            <DotsThreeIcon />
          </DropdownMenuTrigger>

          <DropdownMenuContent>
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
                            visibility: visibility as ExperienceVisibility,
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

            <DropdownMenuItem>
              <TrashIcon />
              {t('menu.delete')}
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      )}
    </div>
  );
}
