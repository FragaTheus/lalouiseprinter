"use client";

import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Input } from "@/shared/components/ui/input";
import { useCreateRestaurant } from "../hooks/use-restaurant";

export default function RegisterRestaurantWrapper() {
  const { mutate, isPending } = useCreateRestaurant();
  return (
    <AppForm btnText="Cadastrar" onSubmit={mutate} isPending={isPending}>
      <Field>
        <FieldLabel>Nome do Restaurante</FieldLabel>
        <FieldContent>
          <Input
            type="text"
            name="restaurantName"
            placeholder="Nome do restaurante"
            required
          />
        </FieldContent>
      </Field>
      <Field>
        <FieldLabel>CNPJ</FieldLabel>
        <FieldContent>
          <Input
            type="text"
            name="cnpj"
            placeholder="00.000.000/0000-00"
            required
          />
        </FieldContent>
      </Field>
    </AppForm>
  );
}
