"use client";

import { useParams } from "next/navigation";
import { useReprintSameLabel } from "../hook/use-label";
import AppForm from "@/shared/components/app/app-form";
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Input } from "@/shared/components/ui/input";

export default function ReprintLabelWrapper() {
  const { id: restaurantId, labelId } = useParams<{
    id: string;
    labelId: string;
  }>();
  const { mutate, isPending } = useReprintSameLabel(labelId, restaurantId);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Reimprimir Etiqueta</CardTitle>
        <CardDescription>
          Reimprima a etiqueta selecionada novamente.
        </CardDescription>
        <AppForm
          btnText="Reimprimir Etiqueta"
          onSubmit={mutate}
          isPending={isPending}
        >
          <Field>
            <FieldLabel>Quantidade</FieldLabel>
            <FieldContent>
              <Input name="copies" type="number" />
            </FieldContent>
          </Field>
        </AppForm>
      </CardHeader>
    </Card>
  );
}
