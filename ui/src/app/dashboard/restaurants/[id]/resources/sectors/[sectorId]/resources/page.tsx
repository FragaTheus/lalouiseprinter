import DashSectorWrapper from "@/features/dashboard/wrapper/dash-sector-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function SectorPage() {
  return (
    <AppRouteGuard>
      <DashSectorWrapper />
    </AppRouteGuard>
  );
}
