'use client';

import { ArrowLeftIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { use } from 'react';

import { useCreateJobApplication } from '@/api/__generated__/applications/applications';
import { useGetJobPosting } from '@/api/__generated__/jobs/jobs';
import { Button } from '@/components/ui/button';
import { useSession } from '@/lib/auth/client';

interface JobPostingPageProps {
  params: Promise<{
    id: string;
    postingId: string;
  }>;
}

export default function JobPostingPage({ params }: JobPostingPageProps) {
  const { id: companyId, postingId } = use(params);
  const searchParams = useSearchParams();
  const t = useTranslations('pages.company.job-postings.posting');

  const router = useRouter();

  const { data: session } = useSession();

  const queryString = searchParams.toString();
  const backUrl = `/company/${companyId}/jobs${queryString ? `?${queryString}` : ''}`;

  const { data: posting, isLoading } = useGetJobPosting(companyId, postingId);
  const { mutate: createJobApplication } = useCreateJobApplication();

  const handleSaveToMyApplicationsButtonClick = () => {
    if (!session) {
      router.push('/auth/login');
    } else {
      createJobApplication(
        {
          data: {
            companyId,
            jobPostingId: postingId,
          },
        },
        {
          onSuccess: ({ data }) => {
            router.push(`/profile/me/applications/${data.applicationId}`);
          },
        },
      );
    }
  };

  if (isLoading) {
    return (
      <div className="p-8 text-center">
        <p className="text-muted-foreground">{t('loading')}</p>
      </div>
    );
  }

  if (!posting) {
    return (
      <div className="p-8 text-center">
        <p className="text-muted-foreground">{t('not-found')}</p>
        <Link href={backUrl}>
          <Button variant="link" className="mt-4">
            <ArrowLeftIcon className="mr-2" />
            {t('view-all')}
          </Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="p-2">
      <Link href={backUrl}>
        <Button variant="ghost" size="sm">
          <ArrowLeftIcon />
          {t('view-all')}
        </Button>
      </Link>

      <div className="mt-4">
        <h2 className="text-xl">{posting.data.jobTitle}</h2>
      </div>

      <div className="flex justify-end my-4">
        <Button onClick={handleSaveToMyApplicationsButtonClick}>
          {t('save-to-my-applications')}
        </Button>
      </div>

      <div>
        <p>{posting.data.jobDescription}</p>
      </div>
    </div>
  );
}
