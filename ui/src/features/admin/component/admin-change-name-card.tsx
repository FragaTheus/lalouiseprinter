"use client";

import AppForm from "@/shared/components/app/app-form";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Input } from "@/shared/components/ui/input";
import { Info } from "lucide-react";
import { useAdminChangeName } from "../hook/use-admin";
import { useParams } from "next/navigation";

export default function AdminChangeNameCard() {
  const { id } = useParams<{ id: string }>();
  const { mutate, isPending } = useAdminChangeName(id);
  return (
    <Card>
      <CardHeader>
        <CardTitle>Alterar Nome</CardTitle>
        <CardDescription>Altere nome do administrador.</CardDescription>
      </CardHeader>
      <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <AppForm btnText="Alterar nome" onSubmit={mutate} isPending={isPending}>
          <Field>
            <FieldLabel>Nome</FieldLabel>
            <FieldContent>
              <Input
                type="text"
                name="newNickname"
                placeholder="Digite um novo nome para o administrador"
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
