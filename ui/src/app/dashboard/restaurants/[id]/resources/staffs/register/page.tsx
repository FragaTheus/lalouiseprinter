import RegisterStaffWrapper from "@/features/staff/wrapper/register-staff-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function RegisterStaffPage() {
  return (
    <AppRouteGuard allowedRoles={["MANAGER"]}>
      <RegisterStaffWrapper />
    </AppRouteGuard>
  );
}
