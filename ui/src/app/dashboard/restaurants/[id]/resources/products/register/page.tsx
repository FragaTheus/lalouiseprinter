import RegisterProductWrapper from "@/features/product/wrapper/register-product-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";
import AppInputPageLayout from "@/shared/components/layouts/app-input-page-layout";
import {
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";

export default function RegisterProduct() {
  return (
    <AppRouteGuard allowedRoles={["MANAGER"]}>
      <AppInputPageLayout>
        <CardHeader>
          <CardTitle>Novo Produto</CardTitle>
          <CardDescription>
            Preencha as informações do novo produto para este restaurante.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <RegisterProductWrapper />
        </CardContent>
      </AppInputPageLayout>
    </AppRouteGuard>
  );
}
