import AppHeader from "@/shared/components/app/app-header";
import AppLogo from "@/shared/components/app/app-logo";
import AppRouterBack from "@/shared/components/app/app-router-back";
import AppDashActios from "./dash-header-actions";
import DashNavBarWrapper from "../wrapper/dash-navbar-wrapper";

export default function DashboardHeader() {
  return (
    <AppHeader>
      <AppRouterBack />
      <AppLogo href="/dashboard" />
      <div className="w-full max-w-1/2 h-full hidden lg:block">
        <DashNavBarWrapper />
      </div>
      <AppDashActios />
    </AppHeader>
  );
}
