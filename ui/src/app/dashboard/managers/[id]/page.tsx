import ManagerInfoWrapper from "@/features/manager/wrapper/manager-info-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function ManagerInfoPage() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN"]}>
      <ManagerInfoWrapper />
    </AppRouteGuard>
  );
}
