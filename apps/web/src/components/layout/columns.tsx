export function TripleColumnLayout({
  left,
  right,
  main,
}: {
  left?: React.ReactNode;
  right?: React.ReactNode;
  main: React.ReactNode;
}) {
  return (
    <div className="flex flex-col">
      <div className="grow flex pb-16 lg:pb-0 flex gap-5">
        {left && <div className="hidden lg:block w-1/5">{left}</div>}
        <div className="grow">{main}</div>
        {right && <div className="hidden lg:block w-1/5">{right}</div>}
      </div>
    </div>
  );
}
