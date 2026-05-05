import { cn } from "@/lib/utils";

export default function AppPageLayout({
  children,
  className,
}: {
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <div
      className={cn(
        `w-full max-w-7xl h-full px-4 py-4 md:py-6 lg:py-8 mt-14 mx-auto`,
        className,
      )}
    >
      {children}
    </div>
  );
}
