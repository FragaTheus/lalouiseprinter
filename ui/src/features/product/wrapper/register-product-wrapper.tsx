"use client";

import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Input } from "@/shared/components/ui/input";
import { Textarea } from "@/shared/components/ui/textarea";
import { useCreateProduct } from "../hook/use-product";
import { useParams } from "next/navigation";

export default function RegisterProductWrapper() {
  const { id: restaurantId } = useParams<{ id: string }>();
  const { mutate, isPending } = useCreateProduct(restaurantId);

  return (
    <AppForm btnText="Cadastrar" onSubmit={mutate} isPending={isPending}>
      <Field>
        <FieldLabel>Nome</FieldLabel>
        <FieldContent>
          <Input
            type="text"
            name="name"
            placeholder="Nome do produto"
            required
          />
        </FieldContent>
      </Field>
      <Field>
        <FieldLabel>Descrição</FieldLabel>
        <FieldContent>
          <Textarea
            name="description"
            placeholder="Descrição do produto"
            required
          />
        </FieldContent>
      </Field>
    </AppForm>
  );
}
