import { cn } from "@/lib/utils";
import { Card } from "../ui/card";
import AppPageLayout from "./app-page-layout";

export default function AppInputPageLayout({
  children,
  className,
}: {
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <AppPageLayout
      className={cn(
        `flex items-center justify-center min-h-svh lg:mt-0!`,
        className,
      )}
    >
      <Card className="w-full max-w-xs">{children}</Card>
    </AppPageLayout>
  );
}
