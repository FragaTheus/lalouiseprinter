"use client";

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
import { Info } from "lucide-react";

interface AppChangePasswordCardProps {
  title: string;
  isProfile: boolean;
  description: string;
  mutate: (data: any) => void;
  isLoading: boolean;
}

export default function AppChangePasswordCard({
  title,
  description,
  isProfile,
  mutate,
  isLoading,
}: AppChangePasswordCardProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
        <CardDescription>{description}</CardDescription>
      </CardHeader>
      <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <AppForm
          onSubmit={mutate}
          btnText="Alterar senha"
          isPending={isLoading}
        >
          {isProfile && (
            <Field>
              <FieldLabel>Senha atual</FieldLabel>
              <FieldContent>
                <Input
                  type="password"
                  name="currentPassword"
                  placeholder="******"
                  required
                />
              </FieldContent>
            </Field>
          )}

          <Field>
            <FieldLabel>Nova senha</FieldLabel>
            <FieldContent>
              <Input
                type="password"
                name="newPassword"
                placeholder="******"
                required
              />
            </FieldContent>
          </Field>

          <Field>
            <FieldLabel>Confirmar nova senha</FieldLabel>
            <FieldContent>
              <Input
                type="password"
                name="confirmNewPassword"
                placeholder="******"
                required
              />
            </FieldContent>
          </Field>
        </AppForm>
        <div className="bg-secondary/10 text-secondary p-4 rounded-md">
          <Info />
          <p className="mt-2">Senha deve ter entre 08 e 16 caracteres</p>
          <p className="mt-2">
            Senha deve conter pelo menos uma letra maiúscula
          </p>
          <p className="mt-2">Senha deve conter pelo menos um número</p>
          <p className="mt-2">
            Senha deve conter pelo menos um caractere especial
          </p>
        </div>
      </CardContent>
    </Card>
  );
}
