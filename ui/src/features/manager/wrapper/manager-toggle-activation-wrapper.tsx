"use client";

import AppToggleActivation from "@/shared/components/app/app-toggle-activation";
import {
  useDeactivateManager,
  useReactivateManager,
} from "../hooks/use-manager";
import { useParams } from "next/navigation";

export default function ManagerToggleActivationWrapper({
  isActive,
}: {
  isActive: boolean;
}) {
  const { managerId, id: restaurantId } = useParams<{
    managerId: string;
    id: string;
  }>();
  const deactivate = useDeactivateManager(restaurantId, managerId);
  const reactivate = useReactivateManager(restaurantId, managerId);

  return (
    <AppToggleActivation
      isActive={isActive}
      active={{
        text: "Desativar gerente",
        mutate: deactivate.mutate,
      }}
      inactive={{
        text: "Ativar gerente",
        mutate: reactivate.mutate,
      }}
    />
  );
}
