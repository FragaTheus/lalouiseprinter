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
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Input } from "@/shared/components/ui/input";
import { useSectorChangeName } from "../hook/use-sector";

export default function SectorChangeNameWrapper() {
  const { sectorId, id: restaurantId } = useParams<{
    sectorId: string;
    id: string;
  }>();
  const { mutate, isPending } = useSectorChangeName(restaurantId, sectorId);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Alterar nome do setor</CardTitle>
        <CardDescription>Insira um novo nome para o setor</CardDescription>
      </CardHeader>
      <CardContent>
        <AppForm
          btnText="Alterar nome do setor"
          isPending={isPending}
          onSubmit={mutate}
        >
          <Field>
            <FieldLabel>Novo nome</FieldLabel>
            <FieldContent>
              <Input name="newName" placeholder="Novo nome do setor" />
            </FieldContent>
          </Field>
        </AppForm>
      </CardContent>
    </Card>
  );
}
