"use client";

import { useParams } from "next/navigation";
import { useAdminChangePassword } from "../hook/use-admin";
import AppChangePasswordCard from "@/shared/components/app/app-password-card";

export default function AdminChangePasswordWrapper({
  isProfile,
}: {
  isProfile: boolean;
}) {
  const { id } = useParams<{ id: string }>();
  const { mutate, isPending } = useAdminChangePassword(id);

  return (
    <AppChangePasswordCard
      title="Alterar senha do administrador"
      description="Insira uma nova senha e confirme para criar uma nova senha."
      mutate={mutate}
      isLoading={isPending}
      isProfile={isProfile}
    />
  );
}
