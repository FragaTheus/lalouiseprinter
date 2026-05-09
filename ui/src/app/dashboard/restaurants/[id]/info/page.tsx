import RestaurantInfoWrapper from "@/features/restaurant/wrapper/restaurant-info-wrapper";
import AppRouteGuard from "@/shared/components/app/app-route-guard";

export default function RestaurantInfoPage() {
  return (
    <AppRouteGuard>
      <RestaurantInfoWrapper />
    </AppRouteGuard>
  );
}
