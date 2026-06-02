import { cn } from "@/lib/utils";

export default function SectionLayout({
  sectionClassName,
  containerClassName,
  children,
}: {
  sectionClassName?: string;
  containerClassName?: string;
  children: React.ReactNode;
}) {
  return (
    <section
      className={cn(
        `w-full py-18 md:py-20 lg:py-22 xl:py-24`,
        sectionClassName,
      )}
    >
      <div className={cn("w-full max-w-7xl px-4 m-auto", containerClassName)}>
        {children}
      </div>
    </section>
  );
}
