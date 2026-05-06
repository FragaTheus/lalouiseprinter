"use client";

import AppToggleActivation from "@/shared/components/app/app-toggle-activation";
import { useDeactiveAdmin, useReactiveAdmin } from "../hook/use-admin";
import { useParams } from "next/navigation";

export default function AdminToggleActivationWrapper({
  isActive,
}: {
  isActive: boolean;
}) {
  const { id } = useParams<{ id: string }>();
  const deactivate = useDeactiveAdmin(id);
  const reactivate = useReactiveAdmin(id);

  return (
    <AppToggleActivation
      isActive={isActive}
      active={{
        text: "Desativar administrador",
        mutate: deactivate.mutate,
      }}
      inactive={{
        text: "Ativar administrador",
        mutate: reactivate.mutate,
      }}
    />
  );
}
