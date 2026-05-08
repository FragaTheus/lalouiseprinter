"use client";

import { useParams } from "next/navigation";
import { useManagerChangePassword } from "../hooks/use-manager";
import AppChangePasswordCard from "@/shared/components/app/app-password-card";

export default function ManagerChangePasswordWrapper({
  isProfile,
}: {
  isProfile: boolean;
}) {
  const { managerId, id: restaurantId } = useParams<{
    managerId: string;
    id: string;
  }>();
  const { mutate, isPending } = useManagerChangePassword(
    restaurantId,
    managerId,
  );

  return (
    <AppChangePasswordCard
      title="Alterar senha do gerente"
      description="Insira uma nova senha e confirme para criar uma nova senha."
      mutate={mutate}
      isLoading={isPending}
      isProfile={isProfile}
    />
  );
}
