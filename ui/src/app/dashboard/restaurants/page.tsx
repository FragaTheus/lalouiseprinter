import RestaurantsWrapper from "@/features/restaurant/wrapper/restaurants-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function RestaurantsList() {
  return (
    <AppRouteGuard allowedRoles={["ADMIN"]}>
      <RestaurantsWrapper />
    </AppRouteGuard>
  );
}
