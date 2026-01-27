'use client';

import dynamic from 'next/dynamic';
import { ComponentType } from 'react';

import { ModalType } from '@/constants/modal';
import { useModal } from '@/hooks/store';

const modals: {
  [K in ModalType]: ComponentType<{}>;
} = {
  [ModalType.SETTINGS]: dynamic(() => import('../modals/SettingsModal')),
};

export default function ModalProvider() {
  const { isOpen, type } = useModal();

  if (!isOpen || type === null) {
    return null;
  }

  const ModalComponent = modals[type];
  return <ModalComponent />;
}
