import SectorInfoWrapper from "@/features/sector/wrapper/sector-info-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function SectorInfoPage() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN", "MANAGER", "STAFF"]}>
      <SectorInfoWrapper />
    </AppRouteGuard>
  );
}
