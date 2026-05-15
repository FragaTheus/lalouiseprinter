"use client";

import AppToggleActivation from "@/shared/components/app/app-toggle-activation";
import { useDeactivateStaff, useReactivateStaff } from "../hooks/use-staff";
import { useParams } from "next/navigation";

export default function StaffToggleActivationWrapper({
  isActive,
}: {
  isActive: boolean;
}) {
  const { id: restaurantId, staffId } = useParams<{
    id: string;
    staffId: string;
  }>();
  const deactivate = useDeactivateStaff(restaurantId, staffId);
  const reactivate = useReactivateStaff(restaurantId, staffId);

  return (
    <AppToggleActivation
      isActive={isActive}
      active={{
        text: "Desativar colaborador",
        mutate: deactivate.mutate,
      }}
      inactive={{
        text: "Ativar colaborador",
        mutate: reactivate.mutate,
      }}
    />
  );
}
