export enum ErrorCode {
  USER_NOT_FOUND = 'USER_NOT_FOUND',
  USER_ALREADY_EXISTS = 'USER_ALREADY_EXISTS',
  PROVIDER_NOT_FOUND = 'PROVIDER_NOT_FOUND',
  MEDIA_NOT_FOUND = 'MEDIA_NOT_FOUND',
  MEDIA_UPLOAD_FAILED = 'MEDIA_UPLOAD_FAILED',
  MESSAGE_TO_SELF = 'MESSAGE_TO_SELF',
}

export default class SyncError extends Error {
  public readonly code: ErrorCode;

  constructor(message: string, code: ErrorCode) {
    super(message);

    Object.setPrototypeOf(this, SyncError.prototype);
    Error.captureStackTrace(this, SyncError);

    this.code = code;
  }
}
