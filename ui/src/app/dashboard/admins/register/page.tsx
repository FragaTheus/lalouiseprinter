import RegisterAdminWrapper from "@/features/admin/wrapper/register-admin-wrapper";
import AppInputPageLayout from "@/shared/components/layouts/app-input-page-layout";
import {
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";

import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function RegisterAdmin() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN"]}>
      <AppInputPageLayout>
        <CardHeader>
          <CardTitle>Novo Administrador</CardTitle>
          <CardDescription>
            Insira os dados necessários para registrar um novo administrador.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <RegisterAdminWrapper />
        </CardContent>
      </AppInputPageLayout>
    </AppRouteGuard>
  );
}
