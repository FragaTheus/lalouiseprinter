import RegisterRestaurantWrapper from "@/features/restaurant/wrapper/register-restaurant-wrapper";
import AppInputPageLayout from "@/shared/components/layouts/app-input-page-layout";
import {
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";

import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function RegisterRestaurant() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN"]}>
      <AppInputPageLayout>
        <CardHeader>
          <CardTitle>Novo Restaurante</CardTitle>
          <CardDescription>
            Preencha as informações do estabelecimento para cadastrá-lo na
            plataforma de gestão.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <RegisterRestaurantWrapper />
        </CardContent>
      </AppInputPageLayout>
    </AppRouteGuard>
  );
}
