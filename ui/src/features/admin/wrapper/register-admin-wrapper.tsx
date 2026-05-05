"use client";

import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Input } from "@/shared/components/ui/input";
import { useRegisterAdmin } from "../hook/use-admin";

export default function RegisterAdminWrapper() {
  const { mutate, isPending } = useRegisterAdmin();
  return (
    <AppForm btnText="Cadastrar" onSubmit={mutate} isPending={isPending}>
      <Field>
        <FieldLabel>Nome</FieldLabel>
        <FieldContent>
          <Input
            type="text"
            name="nickname"
            placeholder="Nome do administrador"
            required
          />
        </FieldContent>
      </Field>
      <Field>
        <FieldLabel>Email</FieldLabel>
        <FieldContent>
          <Input
            type="email"
            name="email"
            placeholder="email@exemplo.com"
            required
          />
        </FieldContent>
      </Field>
      <Field>
        <FieldLabel>Senha</FieldLabel>
        <FieldContent>
          <Input
            type="password"
            name="password"
            placeholder="******"
            required
          />
        </FieldContent>
      </Field>
      <Field>
        <FieldLabel>Confirmar senha</FieldLabel>
        <FieldContent>
          <Input
            type="password"
            name="confirmPassword"
            placeholder="******"
            required
          />
        </FieldContent>
      </Field>
    </AppForm>
  );
}
