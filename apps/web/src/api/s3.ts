export type UploadFileResponse = Response;

export function uploadFileToS3({
  uploadUrl,
  file,
}: {
  uploadUrl: string;
  file: File;
}): Promise<UploadFileResponse> {
  return fetch(uploadUrl, {
    method: 'PUT',
    headers: {
      'Content-Type': file.type,
    },
    body: file,
  });
}
