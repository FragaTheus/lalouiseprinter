import ManagersWrapper from "@/features/manager/wrapper/managers-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function RestaurantManagers() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN", "MANAGER"]}>
      <ManagersWrapper />
    </AppRouteGuard>
  );
}
