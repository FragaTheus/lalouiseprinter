"use client";

import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Input } from "@/shared/components/ui/input";
import useLogin from "../hooks/useLogin";

export default function LoginWrapper() {
  const { mutate, isLoading } = useLogin();
  return (
    <AppForm isPending={isLoading} btnText="Entrar" onSubmit={mutate}>
      <Field>
        <FieldLabel>Email</FieldLabel>
        <FieldContent>
          <Input
            type="email"
            placeholder="Digite seu email"
            required
            name="email"
          />
        </FieldContent>
      </Field>
      <Field>
        <FieldLabel>Senha</FieldLabel>
        <FieldContent>
          <Input
            type="password"
            placeholder="Digite sua senha"
            required
            name="password"
          />
        </FieldContent>
      </Field>
    </AppForm>
  );
}
