'use client';

import { ArrowSquareOutIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import useGetProviderQuery from '@/features/provider/api/get-provider';
import { ProviderType, SchoolType } from '@/types/provider';

interface ProviderAboutProps {
  id: string;
}

interface DetailRow {
  label: string;
  value: string;
}

const KR_LOADING = '\uBD88\uB7EC\uC624\uB294 \uC911...';
const KR_FALLBACK = '\uCD94\uAC00 \uC608\uC815';
const KR_BASE_INFO = '\uAE30\uBCF8 \uC815\uBCF4';
const KR_WEBSITE = '\uC6F9\uC0AC\uC774\uD2B8';
const KR_INDUSTRY = '\uC5C5\uACC4';
const KR_SIZE = '\uADDC\uBAA8';
const KR_HEADQUARTERS = '\uBCF8\uC0AC';
const KR_LOCATION = '\uC704\uCE58';
const KR_SUBCATEGORY = '\uC138\uBD80 \uBD84\uC57C';

export default function ProviderAbout({ id }: ProviderAboutProps) {
  const tProvider = useTranslations('pages.provider');
  const tCreateSchool = useTranslations('pages.create-school');

  const { data: provider } = useGetProviderQuery(id);

  if (!provider) {
    return <div className="p-4 text-sm text-muted-foreground">{KR_LOADING}</div>;
  }

  const detailedIntroduction = KR_FALLBACK;
  const website = provider.contactInfo?.trim() || KR_FALLBACK;
  const industry = provider.industry?.trim() || KR_FALLBACK;
  const size = KR_FALLBACK;
  const location = KR_FALLBACK;
  const category = KR_FALLBACK;

  const locationLabel =
    provider.type === ProviderType.COMPANY ? KR_HEADQUARTERS : KR_LOCATION;

  const schoolType = getSchoolTypeLabel(
    provider.schoolType ?? null,
    tCreateSchool,
    KR_FALLBACK,
  );

  const detailRows: DetailRow[] = [{ label: KR_WEBSITE, value: website }];

  if (provider.type === ProviderType.COMPANY) {
    detailRows.push({ label: KR_INDUSTRY, value: industry });
  }

  if (provider.type === ProviderType.SCHOOL) {
    detailRows.push({
      label: tCreateSchool('form.school-type.label'),
      value: schoolType,
    });
  }

  if (provider.type !== ProviderType.SCHOOL) {
    detailRows.push({ label: KR_SIZE, value: size });
  }

  detailRows.push({ label: locationLabel, value: location });

  if (provider.type !== ProviderType.SCHOOL) {
    detailRows.push({ label: KR_SUBCATEGORY, value: category });
  }

  return (
    <div className="flex flex-col gap-6 p-5 md:gap-8 md:p-6">
      <section className="relative overflow-hidden rounded-2xl border bg-gradient-to-br from-muted/45 via-background to-background p-5 md:p-6">
        <div className="pointer-events-none absolute -right-16 -top-16 h-40 w-40 rounded-full bg-primary/5 blur-3xl" />

        <div className="relative space-y-5">
          <h3 className="text-2xl font-semibold tracking-tight">
            {tProvider('sections.about')}
          </h3>

          <div className="space-y-1.5">
            <p className="text-sm leading-7 whitespace-pre-wrap text-muted-foreground">
              {detailedIntroduction}
            </p>
          </div>
        </div>
      </section>

      <section className="rounded-2xl border bg-background/70 p-4 md:p-6">
        <p className="mb-2 text-xs font-semibold tracking-[0.08em] text-muted-foreground">
          {KR_BASE_INFO}
        </p>

        <dl className="divide-y divide-border/70">
          {detailRows.map((row) => (
            <FieldRow key={row.label} label={row.label} value={row.value} />
          ))}
        </dl>
      </section>
    </div>
  );
}

interface FieldRowProps {
  label: string;
  value: string;
}

function FieldRow({ label, value }: FieldRowProps) {
  const isUrl = /^https?:\/\//i.test(value);

  return (
    <div className="grid gap-2 py-3 md:grid-cols-[170px_1fr] md:gap-8">
      <dt className="text-sm font-medium text-foreground">{label}</dt>
      <dd className="min-w-0 text-sm text-muted-foreground">
        {isUrl ? (
          <a
            href={value}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-1.5 break-all text-blue-600 hover:underline"
          >
            <span>{value}</span>
            <ArrowSquareOutIcon className="mt-0.5 size-4 shrink-0" />
          </a>
        ) : (
          <span className="break-all">{value}</span>
        )}
      </dd>
    </div>
  );
}

function getSchoolTypeLabel(
  schoolType: SchoolType | null,
  t: ReturnType<typeof useTranslations>,
  fallbackValue: string,
) {
  switch (schoolType) {
    case SchoolType.UNIVERSITY:
      return t('form.school-type.types.UNIVERSITY');
    case SchoolType.HIGH_SCHOOL:
      return t('form.school-type.types.HIGH_SCHOOL');
    default:
      return fallbackValue;
  }
}
