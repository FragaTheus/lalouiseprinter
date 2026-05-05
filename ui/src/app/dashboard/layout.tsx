import DashboardHeader from "@/features/dashboard/components/dash-header";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <AppRouteGuard>
      <DashboardHeader />
      {children}
    </AppRouteGuard>
  );
}
