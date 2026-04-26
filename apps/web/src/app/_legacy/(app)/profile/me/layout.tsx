interface MyProfileLayoutProps {
  content: React.ReactNode;
  left: React.ReactNode;
  right: React.ReactNode;
}

export default function MyProfileLayout({
  content,
  left,
  right,
}: MyProfileLayoutProps) {
  return (
    <div className="flex mx-auto max-w-7xl gap-4">
      <div className="hidden lg:block w-1/5">{left}</div>

      <div className="grow">{content}</div>

      <div className="hidden lg:block w-1/5">{right}</div>
    </div>
  );
}
