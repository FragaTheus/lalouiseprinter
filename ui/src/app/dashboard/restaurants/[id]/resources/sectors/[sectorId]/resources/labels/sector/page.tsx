import LabelsSectorWrapper from "@/features/label/wrapper/labels-sector-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function LabelsSectorPage() {
  return (
    <AppRouteGuard>
      <LabelsSectorWrapper />
    </AppRouteGuard>
  );
}
