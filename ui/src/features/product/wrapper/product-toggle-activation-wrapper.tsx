"use client";

import AppToggleActivation from "@/shared/components/app/app-toggle-activation";
import { useDeleteProduct, useReactivateProduct } from "../hook/use-product";
import { useParams } from "next/navigation";

export default function ProductToggleActivationWrapper({
  isActive,
}: {
  isActive: boolean;
}) {
  const { productId, id: restaurantId } = useParams<{
    productId: string;
    id: string;
  }>();
  const deactivate = useDeleteProduct(restaurantId, productId);
  const reactivate = useReactivateProduct(restaurantId, productId);

  return (
    <AppToggleActivation
      isActive={isActive}
      active={{
        text: "Desativar produto",
        mutate: deactivate.mutate,
      }}
      inactive={{
        text: "Ativar produto",
        mutate: reactivate.mutate,
      }}
    />
  );
}
