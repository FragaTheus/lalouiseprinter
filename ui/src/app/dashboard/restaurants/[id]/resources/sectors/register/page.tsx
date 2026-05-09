import RegisterSectorWrapper from "@/features/sector/wrapper/register-sector-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";
import AppInputPageLayout from "@/shared/components/layouts/app-input-page-layout";
import {
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";

export default function RegisterSector() {
  return (
    <AppRouteGuard allowedRoles={["MANAGER"]}>
      <AppInputPageLayout>
        <CardHeader>
          <CardTitle>Novo Setor</CardTitle>
          <CardDescription>
            Preencha as informações do novo setor para este restaurante.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <RegisterSectorWrapper />
        </CardContent>
      </AppInputPageLayout>
    </AppRouteGuard>
  );
}
