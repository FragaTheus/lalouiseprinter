"use client";

import AppForm from "@/shared/components/app/app-form";
import AppLookupModal from "@/shared/components/app/app-lookup-modal";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Input } from "@/shared/components/ui/input";
import { useCreateStaff } from "../hooks/use-staff";
import { useSectorListInfinite } from "@/features/sector/hook/use-sector";
import { useParams } from "next/navigation";
import { useState } from "react";
import AppInputPageLayout from "@/shared/components/layouts/app-input-page-layout";
import {
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";

export default function RegisterStaffWrapper() {
  const { id: restaurantId } = useParams<{ id: string }>();
  const { mutate, isPending } = useCreateStaff();

  const [sectorTerm, setSectorTerm] = useState<string | undefined>(undefined);

  const {
    data: sectorData,
    isLoading: isSectorLoading,
    fetchNextPage: fetchNextSectorPage,
    hasNextPage: hasSectorNextPage,
    isFetchingNextPage: isFetchingSectorNextPage,
  } = useSectorListInfinite(restaurantId, { term: sectorTerm, active: true });

  const sectorOptions = sectorData?.pages.map((page) =>
    page.content.map((sector) => ({
      restaurantId: sector.id,
      name: sector.name,
    })),
  );

  return (
    <AppInputPageLayout>
      <CardHeader>
        <CardTitle>Cadastrar Colaborador</CardTitle>
        <CardDescription>
          Preencha os campos abaixo para cadastrar um novo colaborador.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <AppForm btnText="Cadastrar" onSubmit={mutate} isPending={isPending}>
          <Field>
            <FieldLabel>Nome</FieldLabel>
            <FieldContent>
              <Input
                type="text"
                name="nickname"
                placeholder="Nome do colaborador"
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
          <AppLookupModal
            label="Setor"
            name="sectorId"
            placeholder="Selecione um setor"
            options={sectorOptions}
            onSearch={(value) => setSectorTerm(value || undefined)}
            isLoading={isSectorLoading}
            hasNextPage={hasSectorNextPage}
            fetchNextPage={fetchNextSectorPage}
            isFetchingNextPage={isFetchingSectorNextPage}
          />
        </AppForm>
      </CardContent>
    </AppInputPageLayout>
  );
}
