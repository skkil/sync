import Image from 'next/image';

function Icon() {
  return (
    <Image
      src="/assets/icons/sync_logo.svg"
      alt="logo"
      width={20}
      height={20}
    />
  );
}

function Title() {
  return <div className="text-2xl font-light mb-1">sync</div>;
}

export function Logo() {
  return (
    <div>
      <div className="lg:hidden">
        <Icon />
      </div>

      <div className="hidden lg:block">
        <div className="flex gap-2 items-center">
          <Icon />
          <Title />
        </div>
      </div>
    </div>
  );
}
