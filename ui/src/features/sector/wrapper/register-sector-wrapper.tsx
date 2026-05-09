"use client";

import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Input } from "@/shared/components/ui/input";
import { Textarea } from "@/shared/components/ui/textarea";
import AppStorageSelect from "@/shared/components/app/app-storage-select";
import { useCreateSector } from "../hook/use-sector";
import { useParams } from "next/navigation";

export default function RegisterSectorWrapper() {
  const { id: restaurantId } = useParams<{ id: string }>();
  const { mutate, isPending } = useCreateSector(restaurantId);

  return (
    <AppForm btnText="Cadastrar" onSubmit={mutate} isPending={isPending}>
      <Field>
        <FieldLabel>Nome</FieldLabel>
        <FieldContent>
          <Input type="text" name="name" placeholder="Nome do setor" required />
        </FieldContent>
      </Field>
      <Field>
        <FieldLabel>Descrição</FieldLabel>
        <FieldContent>
          <Textarea
            name="description"
            placeholder="Descrição do setor"
            required
          />
        </FieldContent>
      </Field>
      <AppStorageSelect name="storages" label="Tipos de Armazenamento" />
    </AppForm>
  );
}
