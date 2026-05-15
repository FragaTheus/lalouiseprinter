"use client";

import { useParams } from "next/navigation";
import { useStaffChangeName } from "../hooks/use-staff";
import AppChangeNameCard from "@/shared/components/app/app-form-card";

export default function StaffChangeNameWrapper() {
  const { id: restaurantId, staffId } = useParams<{
    id: string;
    sectorId: string;
    staffId: string;
  }>();
  const { mutate, isPending } = useStaffChangeName(restaurantId, staffId);

  return (
    <AppChangeNameCard
      title="Alterar nome do colaborador"
      description="Altere o nome de exibição do colaborador"
      mutate={mutate}
      isPending={isPending}
    />
  );
}
