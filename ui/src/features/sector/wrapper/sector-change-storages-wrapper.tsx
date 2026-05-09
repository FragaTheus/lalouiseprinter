"use client";

import { useParams } from "next/navigation";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import AppForm from "@/shared/components/app/app-form";
import AppStorageSelect from "@/shared/components/app/app-storage-select";
import { useUpdateSectorStorages } from "../hook/use-sector";

export default function SectorChangeStoragesWrapper() {
  const { sectorId, id: restaurantId } = useParams<{
    sectorId: string;
    id: string;
  }>();
  const { mutate, isPending } = useUpdateSectorStorages(restaurantId, sectorId);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Alterar armazenamentos do setor</CardTitle>
        <CardDescription>
          Selecione os novos tipos de armazenamento para este setor
        </CardDescription>
      </CardHeader>
      <CardContent>
        <AppForm
          btnText="Alterar armazenamentos"
          isPending={isPending}
          onSubmit={mutate}
        >
          <AppStorageSelect name="storages" label="Tipos de Armazenamento" />
        </AppForm>
      </CardContent>
    </Card>
  );
}
