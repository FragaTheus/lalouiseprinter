"use client";

import { useParams } from "next/navigation";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { useProductChangeName } from "../hook/use-product";
import { Input } from "@/shared/components/ui/input";

export default function ProductChangeNameWrapper() {
  const { productId, id: restaurantId } = useParams<{
    productId: string;
    id: string;
  }>();
  const { mutate, isPending } = useProductChangeName(restaurantId, productId);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Alterar nome do produto</CardTitle>
        <CardDescription>Insira um novo nome para o produto</CardDescription>
      </CardHeader>
      <CardContent>
        <AppForm
          btnText="Alterar nome do produto"
          isPending={isPending}
          onSubmit={mutate}
        >
          <Field>
            <FieldLabel>Novo nome</FieldLabel>
            <FieldContent>
              <Input name="newProductName" />
            </FieldContent>
          </Field>
        </AppForm>
      </CardContent>
    </Card>
  );
}
