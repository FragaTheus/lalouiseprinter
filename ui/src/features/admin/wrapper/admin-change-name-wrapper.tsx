"use client";

import { useParams } from "next/navigation";
import { useAdminChangeName } from "../hook/use-admin";
import AppChangeNameCard from "@/shared/components/app/app-form-card";

export default function AdminChangeNameWrapper() {
  const { id } = useParams<{ id: string }>();
  const { mutate, isPending } = useAdminChangeName(id);

  return (
    <AppChangeNameCard
      title="Alterar nome do administrador"
      description="Altere o nome do administrador"
      mutate={mutate}
      isPending={isPending}
    />
  );
}
