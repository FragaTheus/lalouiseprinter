import ManagersWrapper from "@/features/manager/wrapper/managers-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function Managers() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN"]}>
      <ManagersWrapper />
    </AppRouteGuard>
  );
}
