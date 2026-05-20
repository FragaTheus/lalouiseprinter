"use client";

import { useParams } from "next/navigation";
import { useReprintLabel } from "../hook/use-label";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Input } from "@/shared/components/ui/input";
import { useUserStore } from "@/store/user-store";
import { LabelStorageSelect } from "../components/label-storage-select";

export default function ReprintLabelStaffWrapper() {
  const { id: restaurantId, labelId: labelId } = useParams<{
    id: string;
    labelId: string;
  }>();
  const { user } = useUserStore();

  const { mutate, isPending } = useReprintLabel(
    restaurantId,
    labelId,
    user?.sectorId,
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
            <FieldLabel>Armazenamento</FieldLabel>
            <FieldContent>
              <LabelStorageSelect
                name="storage"
                restaurantId={restaurantId}
                sectorId={user?.sectorId}
              />
            </FieldContent>
          </Field>
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
