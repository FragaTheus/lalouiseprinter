import LabelsLotWrapper from "@/features/label/wrapper/labels-lot-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function LabelsLotPage() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN", "MANAGER"]}>
      <LabelsLotWrapper />
    </AppRouteGuard>
  );
}
