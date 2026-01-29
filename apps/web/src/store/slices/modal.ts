import { StateCreator } from 'zustand';

import { ModalType } from '@/constants/modal';

export interface ModalState {
  isOpen: boolean;
  type: ModalType | null;
}

export interface ModalActions {
  openModal: (type: ModalType) => void;
  closeModal: () => void;
}

export type ModalSlice = ModalState & ModalActions;

const initialState: ModalState = {
  isOpen: false,
  type: null,
};

export const createModalSlice: StateCreator<ModalSlice, [], [], ModalSlice> = (
  set,
) => ({
  ...initialState,
  openModal: (type: ModalType) =>
    set({
      isOpen: true,
      type,
    }),
  closeModal: () =>
    set({
      isOpen: false,
      type: null,
    }),
});
