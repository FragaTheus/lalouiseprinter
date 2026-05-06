"use client";

import AppChangePasswordCard from "@/shared/components/app/app-password-card";
import { useProfileChangePassword } from "../hooks/use-profile";

export default function ProfileChangePasswordWrapper() {
  const { mutate, isPending } = useProfileChangePassword();
  return (
    <AppChangePasswordCard
      title="Alterar senha"
      description="Confirme sua senha e insira uma nova para alterar a senha"
      isProfile={true}
      mutate={mutate}
      isLoading={isPending}
    />
  );
}
