import type { Meta, StoryObj } from '@storybook/nextjs-vite';
import { useState } from 'react';

import { FileInput } from '.';
import { Avatar, AvatarFallback, AvatarImage } from '../avatar';

const meta = {
  title: 'ui/FileInput',
  component: FileInput,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    accept: {
      control: 'text',
    },
    maxSize: {
      control: 'number',
    },
    maxFiles: {
      control: 'number',
    },
    disabled: {
      control: 'boolean',
    },
  },
  args: {},
} satisfies Meta<typeof FileInput>;

export default meta;
type Story = StoryObj<typeof meta>;

export const AvatarUpload: Story = {
  render: (args) => {
    const [avatar, setAvatar] = useState<File | null>(null);

    return (
      <FileInput
        onFileChange={(files) => {
          const file = files[0];
          if (file) {
            setAvatar(file);
          }
        }}
        {...args}
      >
        <Avatar>
          <AvatarImage src={avatar ? URL.createObjectURL(avatar) : undefined} />
          <AvatarFallback />
        </Avatar>
      </FileInput>
    );
  },
  args: {
    accept: 'image/*',
  },
};
