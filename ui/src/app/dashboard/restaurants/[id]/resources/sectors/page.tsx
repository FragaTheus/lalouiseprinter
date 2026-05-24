import SectorsWrapper from "@/features/sector/wrapper/sectors-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function RestaurantSectors() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN", "MANAGER"]}>
      <SectorsWrapper />
    </AppRouteGuard>
  );
}
