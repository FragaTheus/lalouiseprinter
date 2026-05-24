import RegisterManagerWrapper from "@/features/manager/wrapper/register-manager-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";
import AppInputPageLayout from "@/shared/components/layouts/app-input-page-layout";
import {
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";

export default function RegisterManager() {
  return (
    <AppRouteGuard allowedRoles={["MANAGER", "ADMIN"]}>
      <AppInputPageLayout>
        <CardHeader>
          <CardTitle>Novo Gerente</CardTitle>
          <CardDescription>
            Configure as credenciais de acesso do gerente para este restaurante.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <RegisterManagerWrapper />
        </CardContent>
      </AppInputPageLayout>
    </AppRouteGuard>
  );
}
