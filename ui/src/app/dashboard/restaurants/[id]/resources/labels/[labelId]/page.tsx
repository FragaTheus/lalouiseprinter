import LabelInfoWrapper from "@/features/label/wrapper/label-info-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function LabelInfoPage() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN", "MANAGER", "STAFF"]}>
      <LabelInfoWrapper />
    </AppRouteGuard>
  );
}
