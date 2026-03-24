interface HomeLayoutProps {
  left: React.ReactNode;
  children: React.ReactNode;
  right: React.ReactNode;
}

export default function HomeLayout({ left, children, right }: HomeLayoutProps) {
  return (
    <div className="flex mx-5 max-w-7xl px-4 mx-auto gap-5">
      <div className="hidden lg:block w-1/5">{left}</div>
      <div className="grow">{children}</div>
      <div className="hidden lg:block w-1/5">{right}</div>
    </div>
  );
}
