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
import { useChangeRestaurantName } from "../hooks/use-restaurant";
import { useParams } from "next/navigation";
import { Info } from "lucide-react";

export default function RestaurantChangeNameCard() {
  const { id } = useParams<{ id: string }>();
  const { mutate, isPending } = useChangeRestaurantName(id);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Alterar nome do restaurante</CardTitle>
        <CardDescription>
          Atualize o nome de exibição do restaurante na plataforma
        </CardDescription>
      </CardHeader>
      <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <AppForm btnText="Alterar nome" onSubmit={mutate} isPending={isPending}>
          <Field>
            <FieldLabel>Novo nome</FieldLabel>
            <FieldContent>
              <Input
                type="text"
                name="restaurantName"
                placeholder="Ex: La Louise Bistrô"
                required
              />
            </FieldContent>
          </Field>
        </AppForm>
        <div className="bg-secondary/10 text-secondary p-4 rounded-md flex flex-col gap-2">
          <Info />
          <p className="font-semibold">Regras para o nome do restaurante:</p>
          <ul className="text-sm list-disc list-inside space-y-1">
            <li>Entre 2 e 80 caracteres</li>
            <li>Letras (incluindo acentuadas), números e espaços</li>
            <li>
              Caracteres especiais permitidos:{" "}
              <code className="text-xs">{`' . - &`}</code>
            </li>
            <li>Não pode ser vazio ou conter apenas espaços</li>
          </ul>
        </div>
      </CardContent>
    </Card>
  );
}
