export interface GetAuthenticatedUserResponse {
  userId: string;
  fullName: string;
  email: string;
}

export interface UpdateProfileRequest {
  name?: string;
}
