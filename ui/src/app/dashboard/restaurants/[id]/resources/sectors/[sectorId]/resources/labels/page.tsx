import LabelsSectorWrapper from "@/features/label/wrapper/labels-sector-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function LabelsPage() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN", "MANAGER", "STAFF"]}>
      <LabelsSectorWrapper />
    </AppRouteGuard>
  );
}
