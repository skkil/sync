'use client';

import { ArrowSquareOutIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';

import useGetProviderQuery from '@/features/provider/api/get-provider';
import { ProviderType, SchoolType } from '@/types/provider';

import ProviderAboutEditDialog from './ProviderAboutEditDialog';

interface ProviderAboutProps {
  id: string;
  showEditButton?: boolean;
}

interface DetailRow {
  label: string;
  value: string;
}

export default function ProviderAbout({
  id,
  showEditButton = false,
}: ProviderAboutProps) {
  const tProvider = useTranslations('pages.provider');
  const tCreateSchool = useTranslations('pages.create-school');

  const { data: provider } = useGetProviderQuery(id);

  if (!provider) {
    return (
      <div className="p-4 text-sm text-muted-foreground">
        {tProvider('about.loading')}
      </div>
    );
  }

  const longDescriptionFallback = tProvider('about.fallbacks.longDescription');
  const websiteFallback = tProvider('about.fallbacks.website');
  const industryFallback = tProvider('about.fallbacks.industry');
  const sizeFallback = tProvider('about.fallbacks.size');
  const locationFallback = tProvider('about.fallbacks.location');
  const categoryFallback = tProvider('about.fallbacks.subcategory');
  const schoolTypeFallback = tProvider('about.fallbacks.schoolType');

  // Existing backend field: description (currently used as short intro in overview card).
  const description = provider.description?.trim() ?? '';

  // New "About" body field (long description): backend field not introduced yet.
  const longDescription = longDescriptionFallback;
  const website = provider.contactInfo?.trim() || websiteFallback;
  const industry = provider.industry?.trim() || industryFallback;
  const size = sizeFallback;
  const location = locationFallback;
  const category = categoryFallback;

  const locationLabel =
    provider.type === ProviderType.COMPANY
      ? tProvider('about.fields.headquarters')
      : tProvider('about.fields.location');

  const schoolType = getSchoolTypeLabel(
    provider.schoolType ?? null,
    tCreateSchool,
    schoolTypeFallback,
  );

  const detailRows: DetailRow[] = [
    { label: tProvider('about.fields.website'), value: website },
  ];

  if (provider.type === ProviderType.COMPANY) {
    detailRows.push({
      label: tProvider('about.fields.industry'),
      value: industry,
    });
  }

  if (provider.type === ProviderType.SCHOOL) {
    detailRows.push({
      label: tCreateSchool('form.school-type.label'),
      value: schoolType,
    });
  }

  if (provider.type !== ProviderType.SCHOOL) {
    detailRows.push({
      label: tProvider('about.fields.size'),
      value: size,
    });
  }

  detailRows.push({ label: locationLabel, value: location });

  if (provider.type !== ProviderType.SCHOOL) {
    detailRows.push({
      label: tProvider('about.fields.subcategory'),
      value: category,
    });
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h1 className="text-xl">{tProvider('sections.about')}</h1>
        {showEditButton && (
          <ProviderAboutEditDialog
            providerType={provider.type}
            triggerLabel={tProvider('about.actions.edit')}
            defaultValues={{
              description,
              longDescription: '',
              website: provider.contactInfo?.trim() ?? '',
              industry: provider.industry?.trim() ?? '',
              size: '',
              location: '',
              subcategory: '',
            }}
          />
        )}
      </div>

      <p className="mb-6 text-sm leading-7 whitespace-pre-wrap text-muted-foreground">
        {longDescription}
      </p>

      <section className="rounded-2xl border bg-background p-4 md:p-5">
        <p className="mb-2 text-xs font-semibold tracking-[0.08em] text-muted-foreground">
          {tProvider('about.base-info')}
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
            className="inline-flex items-center gap-1.5 break-all text-primary hover:underline"
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
