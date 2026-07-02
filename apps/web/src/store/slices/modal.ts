import { StateCreator } from 'zustand';

import { ModalType } from '@/constants/modal';

export type AuthRequiredIntent =
  | 'like'
  | 'bookmark'
  | 'comment'
  | 'follow'
  | 'write';

export interface AuthRequiredModalPayload {
  intent: AuthRequiredIntent;
  redirectTo?: string;
}

export type ModalPayload = {
  [ModalType.SETTINGS]: undefined;
  [ModalType.AUTH_REQUIRED]: AuthRequiredModalPayload;
};

export interface ModalState {
  isOpen: boolean;
  type: ModalType | null;
  payload: ModalPayload[ModalType] | null;
}

export interface ModalActions {
  openModal: <T extends ModalType>(type: T, payload?: ModalPayload[T]) => void;
  closeModal: () => void;
}

export type ModalSlice = ModalState & ModalActions;

const initialState: ModalState = {
  isOpen: false,
  type: null,
  payload: null,
};

export const createModalSlice: StateCreator<ModalSlice, [], [], ModalSlice> = (
  set,
) => ({
  ...initialState,
  openModal: (type, payload) =>
    set({
      isOpen: true,
      type,
      payload: payload ?? null,
    }),
  closeModal: () =>
    set({
      isOpen: false,
      type: null,
      payload: null,
    }),
});
