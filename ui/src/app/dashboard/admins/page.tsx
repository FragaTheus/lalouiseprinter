import AdminsWrapper from "@/features/admin/wrapper/admins-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function Admins() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN"]}>
      <AdminsWrapper />
    </AppRouteGuard>
  );
}
