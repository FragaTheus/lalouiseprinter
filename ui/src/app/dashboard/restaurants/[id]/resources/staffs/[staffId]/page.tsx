import StaffInfoWrapper from "@/features/staff/wrapper/staff-info-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function StaffInfo() {
  return (
    <AppRouteGuard allowedRoles={["MANAGER", "ADMIN"]}>
      <StaffInfoWrapper />
    </AppRouteGuard>
  );
}
