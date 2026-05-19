import PrintLabelStaffWrapper from "@/features/label/wrapper/print-label-staff-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";
import AppInputPageLayout from "@/shared/components/layouts/app-input-page-layout";
import {
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";

export default function PrintLabelPage() {
  return (
    <AppRouteGuard allowedRoles={["MANAGER", "STAFF"]}>
      <AppInputPageLayout>
        <CardHeader>
          <CardTitle>Nova Etiqueta</CardTitle>
          <CardDescription>
            Preencha as informações para imprimir uma nova etiqueta.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <PrintLabelStaffWrapper />
        </CardContent>
      </AppInputPageLayout>
    </AppRouteGuard>
  );
}
