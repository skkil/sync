'use client';

import { useTranslations } from 'next-intl';
import { useParams } from 'next/navigation';
import { toast } from 'sonner';

import {
  getGetJobApplicationFilesQueryKey,
  useGetJobApplicationFiles,
  useUploadJobApplicationFile,
} from '@/api/__generated__/applications/applications';
import { useUploadMedia } from '@/api/__generated__/media/media';
import { uploadFileToS3 } from '@/api/s3';
import { Button } from '@/components/ui/button';
import { FileInput } from '@/components/ui/input';

type ApplicationFilesTabParams = {
  id: string;
};

export default function ApplicationFilesTab() {
  const { id: applicationId } = useParams<ApplicationFilesTabParams>();
  const t = useTranslations('pages.profile.applications.details.tabs.files');

  const { data: jobApplicationFiles } =
    useGetJobApplicationFiles(applicationId);

  const { mutateAsync: uploadMedia } = useUploadMedia();
  const { mutateAsync: uploadJobApplicationFile } =
    useUploadJobApplicationFile();

  const handleFileChange = async (files: File[]) => {
    if (files.length === 0) {
      return;
    }

    const file = files[0];
    if (!file) {
      return;
    }

    const {
      data: { mediaId, uploadUrl },
    } = await uploadMedia({
      data: {
        fileName: file.name,
        fileSize: file.size,
        mediaType: file.type,
      },
    });

    const { success: uploadSuccess } = await uploadFileToS3({
      file,
      uploadUrl,
    });

    if (!uploadSuccess) {
      toast.error(t('errors.upload-failed'));
      return;
    }

    await uploadJobApplicationFile(
      {
        applicationId,
        data: {
          fileId: mediaId,
        },
      },
      {
        onSuccess: (_data, _variables, _onMutateResult, context) => {
          toast.success(t('upload-success'));

          const queryKey = getGetJobApplicationFilesQueryKey(applicationId);
          context.client.invalidateQueries({
            queryKey,
          });
        },
      },
    );
  };

  if (!jobApplicationFiles) {
    return null;
  }

  const { files } = jobApplicationFiles.data;

  return (
    <div className="flex flex-col gap-4">
      <div className="self-end">
        <FileInput accept="application/pdf" onFileChange={handleFileChange}>
          <Button>{t('upload-file')}</Button>
        </FileInput>
      </div>

      {files.length === 0 ? (
        <div>
          <span>{t('empty')}</span>
        </div>
      ) : (
        files.map((file) => (
          <div key={file.url}>
            <iframe src={file.url} className="w-full" />
          </div>
        ))
      )}
    </div>
  );
}
