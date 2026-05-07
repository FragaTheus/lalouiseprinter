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
            Configure as credenciais de acesso corporativo e permissões de
            segurança para o novo membro da equipe de gestão.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <RegisterAdminWrapper />
        </CardContent>
      </AppInputPageLayout>
    </AppRouteGuard>
  );
}
