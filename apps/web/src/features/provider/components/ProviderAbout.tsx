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

type DetailFieldId =
  | 'website'
  | 'industry'
  | 'size'
  | 'location'
  | 'subcategory'
  | 'schoolType';
type AboutFallbackId = DetailFieldId | 'longDescription';

interface DetailRow {
  id: DetailFieldId;
  label: string;
  value: string;
  isUrl?: boolean;
}

export default function ProviderAbout({
  id,
  showEditButton = false,
}: ProviderAboutProps) {
  const tProvider = useTranslations('pages.provider');
  const { data: provider } = useGetProviderQuery(id);

  if (!provider) {
    return (
      <div className="p-4 text-sm text-muted-foreground">
        {tProvider('about.loading')}
      </div>
    );
  }

  const getFallback = (fieldId: AboutFallbackId) =>
    tProvider(`about.fallbacks.${fieldId}`);

  const longDescription = getFallback('longDescription');
  const websiteValue = provider.contactInfo?.trim() ?? '';
  const industryValue = provider.industry?.trim() ?? '';
  const locationLabel =
    provider.type === ProviderType.COMPANY
      ? tProvider('about.fields.headquarters')
      : tProvider('about.fields.location');
  const schoolType = getSchoolTypeLabel(
    provider.schoolType ?? null,
    tProvider,
    getFallback('schoolType'),
  );

  const detailRows: DetailRow[] = [
    {
      id: 'website',
      label: tProvider('about.fields.website'),
      value: websiteValue || getFallback('website'),
      isUrl: websiteValue.length > 0,
    },
    ...(provider.type === ProviderType.COMPANY
      ? [
          {
            id: 'industry' as const,
            label: tProvider('about.fields.industry'),
            value: industryValue || getFallback('industry'),
          },
        ]
      : []),
    ...(provider.type === ProviderType.SCHOOL
      ? [
          {
            id: 'schoolType' as const,
            label: tProvider('about.fields.schoolType'),
            value: schoolType,
          },
        ]
      : [
          {
            id: 'size' as const,
            label: tProvider('about.fields.size'),
            value: getFallback('size'),
          },
        ]),
    {
      id: 'location',
      label: locationLabel,
      value: getFallback('location'),
    },
    ...(provider.type === ProviderType.SCHOOL
      ? []
      : [
          {
            id: 'subcategory' as const,
            label: tProvider('about.fields.subcategory'),
            value: getFallback('subcategory'),
          },
        ]),
  ];

  return (
    <div>
      <div className="mb-5 flex items-center justify-between">
        <h1 className="text-xl">{tProvider('sections.about')}</h1>
        {showEditButton && <ProviderAboutEditDialog id={id} />}
      </div>

      <p className="mb-6 whitespace-pre-wrap text-sm leading-7 text-muted-foreground">
        {longDescription}
      </p>

      <section className="rounded-2xl border bg-background p-4 md:p-5">
        <p className="mb-2 text-xs font-semibold tracking-[0.08em] text-muted-foreground">
          {tProvider('about.base-info')}
        </p>

        <dl className="divide-y divide-border/70">
          {detailRows.map(({ id: rowId, isUrl, label, value }) => (
            <FieldRow key={rowId} label={label} value={value} isUrl={isUrl} />
          ))}
        </dl>
      </section>
    </div>
  );
}

interface FieldRowProps {
  label: string;
  value: string;
  isUrl?: boolean;
}

function FieldRow({ label, value, isUrl = false }: FieldRowProps) {
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
      return t('about.school-type.types.UNIVERSITY');
    case SchoolType.HIGH_SCHOOL:
      return t('about.school-type.types.HIGH_SCHOOL');
    default:
      return fallbackValue;
  }
}
