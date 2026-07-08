'use client';

import { MagnifyingGlassIcon } from '@phosphor-icons/react';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';

interface SearchQueryInputProps {
  query: string;
  projectHandle?: string;
}

export default function SearchQueryInput({
  query,
  projectHandle,
}: SearchQueryInputProps) {
  const router = useRouter();
  const [value, setValue] = useState(query);

  const handleSearch = () => {
    const trimmed = value.trim();
    if (!trimmed) return;
    const params = new URLSearchParams({ q: trimmed });
    if (projectHandle) params.set('projectHandle', projectHandle);
    router.push(`/search?${params.toString()}`);
  };

  return (
    <InputGroup className="h-14">
      <InputGroupAddon>
        <MagnifyingGlassIcon size={20} />
      </InputGroupAddon>
      <InputGroupInput
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === 'Enter') {
            e.preventDefault();
            handleSearch();
          }
        }}
        className="text-lg"
      />
    </InputGroup>
  );
}
