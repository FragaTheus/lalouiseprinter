"use client";

import AppToggleActivation from "@/shared/components/app/app-toggle-activation";
import { useDeleteSector, useReactivateSector } from "../hook/use-sector";
import { useParams } from "next/navigation";

export default function SectorToggleActivationWrapper({
  isActive,
}: {
  isActive: boolean;
}) {
  const { sectorId, id: restaurantId } = useParams<{
    sectorId: string;
    id: string;
  }>();
  const deactivate = useDeleteSector(restaurantId, sectorId);
  const reactivate = useReactivateSector(restaurantId, sectorId);

  return (
    <AppToggleActivation
      isActive={isActive}
      active={{
        text: "Desativar setor",
        mutate: deactivate.mutate,
      }}
      inactive={{
        text: "Ativar setor",
        mutate: reactivate.mutate,
      }}
    />
  );
}
