"use client";

import AppForm from "@/shared/components/app/app-form";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import { useProfileChangeName } from "../hooks/use-profile";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Input } from "@/shared/components/ui/input";
import { Info } from "lucide-react";

export default function ChangeNameCard() {
  const { mutate, isPending } = useProfileChangeName();
  return (
    <Card>
      <CardHeader>
        <CardTitle>Alterar Nome</CardTitle>
        <CardDescription>Altere seu nome de usuário.</CardDescription>
      </CardHeader>
      <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <AppForm btnText="Alterar nome" onSubmit={mutate} isPending={isPending}>
          <Field>
            <FieldLabel>Nome</FieldLabel>
            <FieldContent>
              <Input
                type="text"
                name="newNickname"
                placeholder="Digite seu novo nome"
                required
              />
            </FieldContent>
          </Field>
        </AppForm>
        <div className="bg-secondary/10 text-secondary p-4 rounded-md">
          <Info />
          <p className="mt-2">
            Nome deve ter entre 03 e 30 caracteres com letras e espaços,
            somente.
          </p>
        </div>
      </CardContent>
    </Card>
  );
}
