import ProductsWrapper from "@/features/product/wrapper/products-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function RestaurantProducts() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN", "MANAGER"]}>
      <ProductsWrapper />
    </AppRouteGuard>
  );
}
