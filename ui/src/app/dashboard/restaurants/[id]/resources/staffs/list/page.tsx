import StaffsWrapper from "@/features/staff/wrapper/staffs-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function StaffPage() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN", "MANAGER"]}>
      <StaffsWrapper />
    </AppRouteGuard>
  );
}
