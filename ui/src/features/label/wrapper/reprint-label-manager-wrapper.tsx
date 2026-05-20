"use client";

import { useParams } from "next/navigation";
import { useReprintLabel } from "../hook/use-label";
import { useState } from "react";
import { useSectorListInfinite } from "@/features/sector/hook/use-sector";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import AppLookupModal from "@/shared/components/app/app-lookup-modal";
import LabelStorageSelectWrapper from "./label-storage-select-wrapper";
import { Input } from "@/shared/components/ui/input";

export default function ReprintLabelManagerWrapper() {
  const { id: restaurantId, labelId: labelId } = useParams<{
    id: string;
    labelId: string;
  }>();

  const { mutate, isPending } = useReprintLabel(restaurantId, labelId);

  const [sectorTerm, setSectorTerm] = useState<string | undefined>(undefined);

  const {
    data: sectorData,
    fetchNextPage: fetchNextSectorPage,
    hasNextPage: hasSectorNextPage,
    isFetchingNextPage: isFetchingSectorNextPage,
  } = useSectorListInfinite(restaurantId, { term: sectorTerm, active: true });

  const sectorOptions = sectorData?.pages.map((page) =>
    page.content.map((sector) => ({
      restaurantId: sector.id,
      name: sector.name,
    })),
  );

  return (
    <Card>
      <CardHeader>
        <CardTitle>Reimprimir sequencia</CardTitle>
        <CardDescription>
          Insira o setor e o armazenamento para reimprimir a etiqueta e a
          quantidade de etiquetas a serem impressar a partid desta etiqueta
          atual.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <AppForm btnText="Reimprimir" isPending={isPending} onSubmit={mutate}>
          <Field>
            <AppLookupModal
              label="Setor"
              name="sectorId"
              placeholder="Selecione um setor"
              options={sectorOptions}
              onSearch={setSectorTerm}
              fetchNextPage={fetchNextSectorPage}
              hasNextPage={hasSectorNextPage}
              isFetchingNextPage={isFetchingSectorNextPage}
            />
          </Field>
          <LabelStorageSelectWrapper restaurantId={restaurantId} />
          <Field>
            <FieldLabel>Quantidade</FieldLabel>
            <FieldContent>
              <Input name="copies" type="number" />
            </FieldContent>
          </Field>
        </AppForm>
      </CardContent>
    </Card>
  );
}
