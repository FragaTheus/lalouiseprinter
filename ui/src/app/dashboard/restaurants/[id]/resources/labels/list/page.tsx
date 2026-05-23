import LabelsWrapper from "@/features/label/wrapper/labels-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function LabelsRestaurantPage() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN", "MANAGER"]}>
      <LabelsWrapper />
    </AppRouteGuard>
  );
}
