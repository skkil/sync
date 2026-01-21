import { EnvelopeIcon, PhoneIcon } from '@phosphor-icons/react/ssr';
import { getTranslations } from 'next-intl/server';

import { Avatar } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import Logo from '@/components/ui/logo';
import { Experience, ExperienceCategory } from '@/types/experience';
import { Profile } from '@/types/profile';
import { Provider, ProviderCategory } from '@/types/provider';

interface ProfileProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function ProfilePage({ params }: ProfileProps) {
  const { id } = await params;

  const t = await getTranslations('pages.profile');

  const profile: Profile = {
    id,
    name: 'Example User',
    bio: 'This is an example user profile.',
    contacts: [
      {
        id: '1',
        type: 'email',
        icon: <EnvelopeIcon />,
        value: 'example@email.com',
      },
      {
        id: '2',
        type: 'phone',
        icon: <PhoneIcon />,
        value: '+1 (555) 123-4567',
      },
    ],
  };

  const providers: Provider[] = [
    {
      id: '1',
      name: 'Sungkyunwan University',
      category: ProviderCategory.SCHOOL,
    },
    {
      id: '2',
      name: 'Samsung',
      category: ProviderCategory.COMPANY,
      industry: 'IT',
    },
  ];

  const experiences: Experience[] = [
    {
      id: '1',
      category: ExperienceCategory.EDUCATION,
      provider: providers[0]!,
      startDate: new Date(),
      endDate: new Date(),
      major: 'Computer Science',
    },
    {
      id: '2',
      category: ExperienceCategory.EMPLOYMENT,
      provider: providers[1]!,
      startDate: new Date(),
      endDate: new Date(),
    },
  ];

  return (
    <>
      <nav className="flex w-full items-center justify-between border-b bg-white p-3 px-8">
        <Logo />
      </nav>

      <div className="flex flex-col gap-4 mt-4">
        <Card className="mx-auto w-full max-w-3xl min-h-96 p-0">
          <CardHeader className="relative h-48 bg-muted">
            <Avatar className="h-32 w-32 bg-card absolute left-8 -bottom-16" />
          </CardHeader>

          <CardContent className="mt-10 mx-4">
            <div className="flex justify-between items-center">
              <h2 className="text-3xl font-bold mt-3">{profile.name}</h2>

              <div className="flex gap-2">
                <Button>{t('header.follow')}</Button>
                <Button variant="outline">{t('header.message')}</Button>
              </div>
            </div>

            <div className="flex flex-col-reverse md:flex-row md:justify-between mt-4 gap-4">
              {profile.bio}

              <div className="flex flex-col">
                {profile.contacts.map((contact) => (
                  <div
                    key={contact.id}
                    className="text-sm text-muted-foreground flex items-center gap-2"
                  >
                    <p>{contact.icon}</p>
                    <p>{contact.value}</p>
                  </div>
                ))}
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="mx-auto w-full max-w-3xl">
          <CardHeader>
            <h1 className="text-xl font-bold">{t('employment.title')}</h1>
            {experiences
              .filter(
                (experience) =>
                  experience.category === ExperienceCategory.EMPLOYMENT,
              )
              .map((employment) => (
                <div key={employment.id}>
                  <h1 className="text-md font-semibold">
                    {employment.provider.name}
                  </h1>
                  {employment.startDate.getFullYear()} -{' '}
                  {employment.endDate.getFullYear()}
                </div>
              ))}
          </CardHeader>
        </Card>

        <Card className="mx-auto w-full max-w-3xl">
          <CardHeader>
            <h1 className="text-xl font-bold">{t('education.title')}</h1>
            {experiences
              .filter(
                (experience) =>
                  experience.category === ExperienceCategory.EDUCATION,
              )
              .map((education) => (
                <div key={education.id}>
                  <h1 className="text-md font-semibold">
                    {education.provider.name}
                  </h1>
                  {education.major && <p>{education.major}</p>}
                  {education.startDate.getFullYear()} -{' '}
                  {education.endDate.getFullYear()}
                </div>
              ))}
          </CardHeader>
        </Card>
      </div>
    </>
  );
}
