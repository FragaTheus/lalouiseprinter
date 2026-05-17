"use client";

import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { useParams } from "next/navigation";
import { useState } from "react";
import {
  useReprintBySectorContext,
  useReprintByInputSector,
} from "../hook/use-label";
import { LabelStorageSelect } from "../components/label-storage-select";
import AppLookupModal from "@/shared/components/app/app-lookup-modal";
import { useSectorListInfinite } from "@/features/sector/hook/use-sector";
import { useUserStore } from "@/store/user-store";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";

export default function ReprintLabelWrapper() {
  const {
    id: restaurantId,
    labelId,
    sectorId,
  } = useParams<{
    id: string;
    labelId: string;
    sectorId: string;
  }>();

  const { user } = useUserStore();
  const hasSectorContext = !!user?.sectorId;

  const reprintByContext = useReprintBySectorContext(
    restaurantId,
    sectorId,
    labelId,
  );

  const [selectedSectorId, setSelectedSectorId] = useState("");
  const reprintByInput = useReprintByInputSector(
    restaurantId,
    labelId,
    selectedSectorId,
  );

  const [sectorTerm, setSectorTerm] = useState<string | undefined>(undefined);

  const {
    data: sectorData,
    fetchNextPage: fetchNextSectorPage,
    hasNextPage: hasSectorNextPage,
    isFetchingNextPage: isFetchingSectorNextPage,
  } = useSectorListInfinite(restaurantId, {
    term: sectorTerm,
    active: true,
  });

  const sectorOptions = sectorData?.pages.map((page) =>
    page.content.map((sector) => ({
      restaurantId: sector.id,
      name: sector.name,
    })),
  );

  const handleSubmit = (data: Record<string, string>) => {
    const payload = { storage: data.storage as never };
    if (hasSectorContext) {
      reprintByContext.mutate(payload);
    } else {
      reprintByInput.mutate(payload);
    }
  };

  const isPending = reprintByContext.isPending || reprintByInput.isPending;

  return (
    <Card>
      <CardHeader>
        <CardTitle>Reimprimir etiqueta</CardTitle>
        <CardDescription>
          Gere uma nova impressão desta etiqueta
        </CardDescription>
      </CardHeader>
      <CardContent>
        <AppForm
          btnText="Reimprimir"
          onSubmit={handleSubmit}
          isPending={isPending}
        >
          {!hasSectorContext && (
            <AppLookupModal
              label="Setor de destino"
              name="sectorId"
              placeholder="Selecione um setor"
              options={sectorOptions}
              onSearch={setSectorTerm}
              fetchNextPage={fetchNextSectorPage}
              hasNextPage={hasSectorNextPage}
              isFetchingNextPage={isFetchingSectorNextPage}
            />
          )}
          <Field>
            <FieldLabel>Armazenamento</FieldLabel>
            <FieldContent>
              <LabelStorageSelect name="storage" />
            </FieldContent>
          </Field>
        </AppForm>
      </CardContent>
    </Card>
  );
}
