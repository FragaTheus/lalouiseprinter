import AdminInfoWrapper from "@/features/admin/wrapper/admin-info-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function AdminInfoPage() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN"]}>
      <AdminInfoWrapper />
    </AppRouteGuard>
  );
}
