import DashboardHeader from "@/features/dashboard/components/dash-header";
import DashNavBarWrapper from "@/features/dashboard/wrapper/dash-navbar-wrapper";
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
      <div className="h-16 bg-popover/50 backdrop-blur-md border-t border-border/20 w-full bottom-0 fixed z-50 lg:hidden">
        <DashNavBarWrapper />
      </div>
    </AppRouteGuard>
  );
}
