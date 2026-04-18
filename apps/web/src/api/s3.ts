import ky from 'ky';

interface S3UploadResponse {
  success: boolean;
}

export async function uploadFileToS3({
  uploadUrl,
  file,
}: {
  uploadUrl: string;
  file: File;
}): Promise<S3UploadResponse> {
  return ky
    .put<void>(uploadUrl, {
      headers: {
        'Content-Type': file.type,
      },
      body: file,
    })
    .then(() => ({
      success: true,
    }))
    .catch(() => ({
      success: false,
    }));
}
