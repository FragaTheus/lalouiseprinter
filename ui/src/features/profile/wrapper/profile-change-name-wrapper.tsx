"use client";

import AppChangeNameCard from "@/shared/components/app/app-change-name-card";
import { useProfileChangeName } from "../hooks/use-profile";

export default function ProfileChangeNameWrapper() {
  const { mutate, isPending } = useProfileChangeName();
  return (
    <AppChangeNameCard
      mutate={mutate}
      isPending={isPending}
      title="Alterar o seu nome de usuario"
      description="Seu nome e exibido para o restante dos operadores"
      placeholder="Nome de exemplo"
    />
  );
}
