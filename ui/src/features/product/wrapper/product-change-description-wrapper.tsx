"use client";

import { useParams } from "next/navigation";
import { useUpdateProductDescription } from "../hook/use-product";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { Textarea } from "@/shared/components/ui/textarea";

export default function ProductChangeDescriptionWrapper() {
  const { productId, id: restaurantId } = useParams<{
    productId: string;
    id: string;
  }>();
  const { mutate, isPending } = useUpdateProductDescription(
    restaurantId,
    productId,
  );

  return (
    <Card>
      <CardHeader>
        <CardTitle>Alterar descrição do produto</CardTitle>
        <CardDescription>
          Insira uma nova descrição para o produto
        </CardDescription>
      </CardHeader>
      <CardContent>
        <AppForm
          btnText="Alterar descrição"
          isPending={isPending}
          onSubmit={mutate}
        >
          <Field>
            <FieldLabel>Nova descrição</FieldLabel>
            <FieldContent>
              <Textarea name="newDescription" />
            </FieldContent>
          </Field>
        </AppForm>
      </CardContent>
    </Card>
  );
}
