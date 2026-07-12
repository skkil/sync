'use client';

/* eslint-disable react-hooks/immutability -- this whole file's job is composing ref callbacks that mutate .current on DOM attach/detach, never during render; the rule can't distinguish that from a render-time mutation */
import { useCallback, useRef } from 'react';

// basically Exclude<React.ClassAttributes<T>["ref"], string>
type UserRef<T> =
  | ((instance: T | null) => void)
  | React.RefObject<T | null>
  | null
  | undefined;

const updateRef = <T>(ref: NonNullable<UserRef<T>>, value: T | null) => {
  if (typeof ref === 'function') {
    ref(value);
  } else if (ref && typeof ref === 'object' && 'current' in ref) {
    // Safe assignment without MutableRefObject
    (ref as { current: T | null }).current = value;
  }
};

export const useComposedRef = <T extends HTMLElement>(
  libRef: React.RefObject<T | null>,
  userRef: UserRef<T>,
) => {
  const prevUserRef = useRef<UserRef<T>>(null);

  return useCallback(
    (instance: T | null) => {
      if (libRef && 'current' in libRef) {
        (libRef as { current: T | null }).current = instance;
      }

      if (prevUserRef.current) {
        updateRef(prevUserRef.current, null);
      }

      prevUserRef.current = userRef;

      if (userRef) {
        updateRef(userRef, instance);
      }
    },
    [libRef, userRef],
  );
};

export default useComposedRef;
