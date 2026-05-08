"use client";

import { useParams } from "next/navigation";
import { useManagerChangeName } from "../hooks/use-manager";
import AppChangeNameCard from "@/shared/components/app/app-form-card";

export default function ManagerChangeNameWrapper() {
  const { managerId, id: restaurantId } = useParams<{
    managerId: string;
    id: string;
  }>();
  const { mutate, isPending } = useManagerChangeName(restaurantId, managerId);

  return (
    <AppChangeNameCard
      title="Alterar nome do gerente"
      description="Altere o nome de exibição do gerente"
      mutate={mutate}
      isPending={isPending}
    />
  );
}
