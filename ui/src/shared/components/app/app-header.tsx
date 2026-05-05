import { cn } from "@/lib/utils";

export default function AppHeader({
  className,
  children,
}: {
  className?: string;
  children: React.ReactNode;
}) {
  return (
    <header
      className={cn(
        `fixed h-14 bg-popover/50 backdrop-blur-sm w-full shadow-md left-0 top-0 z-50`,
        className,
      )}
    >
      <div className="flex items-center justify-between px-4 h-full w-full max-w-7xl mx-auto">
        {children}
      </div>
    </header>
  );
}
