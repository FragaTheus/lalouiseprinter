import AppHeader from "@/shared/components/app/app-header";
import AppLogo from "@/shared/components/app/app-logo";
import AppRouterBack from "@/shared/components/app/app-router-back";
import AppDashActios from "./dash-header-actions";

export default function DashboardHeader() {
  return (
    <AppHeader>
      <AppRouterBack />
      <AppLogo href="/dashboard" />
      <AppDashActios />
    </AppHeader>
  );
}
