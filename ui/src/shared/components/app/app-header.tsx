export default function AppHeader({ children }: { children: React.ReactNode }) {
  return (
    <header className="bg-popover/80 w-full fixed left-0 top-0 shadow-md h-14 z-10">
      <div className="w-full max-w-7xl px-4 h-full flex items-center justify-between mx-auto">
        {children}
      </div>
    </header>
  );
}
