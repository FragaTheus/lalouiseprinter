"use client";

import { useParams } from "next/navigation";
import { useStaffChangePassword } from "../hooks/use-staff";
import AppChangePasswordCard from "@/shared/components/app/app-password-card";

export default function StaffChangePasswordWrapper() {
  const { id: restaurantId, sectorId, staffId } = useParams<{
    id: string;
    sectorId: string;
    staffId: string;
  }>();
  const { mutate, isPending } = useStaffChangePassword(
    restaurantId,
    sectorId,
    staffId,
  );

  return (
    <AppChangePasswordCard
      title="Alterar senha do colaborador"
      description="Insira uma nova senha e confirme para criar uma nova senha."
      mutate={mutate}
      isLoading={isPending}
      isProfile={false}
    />
  );
}
