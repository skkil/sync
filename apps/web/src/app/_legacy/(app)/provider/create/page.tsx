import {
  BuildingOfficeIcon,
  LightbulbIcon,
  StudentIcon,
  TrophyIcon,
} from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { ProviderType } from '@/types/provider';

interface ProviderComponentOptions {
  id: string;
  type: ProviderType;
  icon: React.ReactNode;
  link: string;
}

const providers: ProviderComponentOptions[] = [
  {
    id: 'company',
    type: ProviderType.COMPANY,
    icon: <BuildingOfficeIcon />,
    link: '/provider/create/company',
  },
  {
    id: 'school',
    type: ProviderType.SCHOOL,
    icon: <StudentIcon />,
    link: '/provider/create/school',
  },
];

const otherProviders: ProviderComponentOptions[] = [
  {
    id: 'project',
    type: ProviderType.PROJECT,
    icon: <LightbulbIcon />,
    link: '/provider/create/project',
  },
  {
    id: 'contest',
    type: ProviderType.CONTEST,
    icon: <TrophyIcon />,
    link: '/provider/create/contest',
  },
];

export default async function CreateProviderPage() {
  const t = await getTranslations('pages.create-provider');

  return (
    <div className="flex flex-col items-center py-12 px-4">
      <header className="text-center mb-10 max-w-2xl">
        <h1 className="text-3xl mb-4">{t('title')}</h1>
        <p>{t('description')}</p>
      </header>

      <div className="flex flex-col gap-4">
        {providers.map((provider) => (
          <Link href={provider.link} key={provider.type}>
            <Card className="w-96 hover:outline hover:cursor-pointer">
              <div className="flex items-center px-4">
                <div>{provider.icon}</div>
                <div className="grow">
                  <CardHeader>
                    <CardTitle>{t(`links.${provider.id}.title`)}</CardTitle>
                    <CardDescription>
                      {t(`links.${provider.id}.description`)}
                    </CardDescription>
                  </CardHeader>
                </div>
              </div>
            </Card>
          </Link>
        ))}
      </div>

      <p className="my-6">{t('or-other.label')}</p>

      <div>
        <div className="flex flex-col gap-4">
          {otherProviders.map((provider) => (
            <Link href={provider.link} key={provider.type}>
              <Card className="w-96 hover:outline hover:cursor-pointer">
                <div className="flex items-center px-4">
                  <div>{provider.icon}</div>
                  <div className="grow">
                    <CardHeader>
                      <CardTitle>{t(`links.${provider.id}.title`)}</CardTitle>
                      <CardDescription>
                        {t(`links.${provider.id}.description`)}
                      </CardDescription>
                    </CardHeader>
                  </div>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
}
