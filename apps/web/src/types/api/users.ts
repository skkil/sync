interface GetAuthenticatedUserResponse {
  userId: string;
  fullName: string;
  email: string;
}

interface UpdateProfileRequest {
  name?: string;
}
