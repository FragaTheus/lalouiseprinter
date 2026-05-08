import ProductInfoWrapper from "@/features/product/wrapper/product-info-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function ProductInfoPage() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN", "MANAGER"]}>
      <ProductInfoWrapper />
    </AppRouteGuard>
  );
}
