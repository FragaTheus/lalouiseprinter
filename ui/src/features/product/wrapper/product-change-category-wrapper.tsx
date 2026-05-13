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
import { useUpdateProductCategory } from "../hook/use-product";
import { CategorySelect } from "../components/product-category-select";

export default function ProductChangeCategoryWrapper() {
  const { productId, id: restaurantId } = useParams<{
    productId: string;
    id: string;
  }>();
  const { mutate, isPending } = useUpdateProductCategory(
    restaurantId,
    productId,
  );

  return (
    <Card>
      <CardHeader>
        <CardTitle>Alterar categoria do produto</CardTitle>
        <CardDescription>
          Insira uma nova categoria para o produto
        </CardDescription>
      </CardHeader>
      <CardContent>
        <AppForm
          btnText="Alterar categoria"
          isPending={isPending}
          onSubmit={mutate}
        >
          <Field>
            <FieldLabel>Nova categoria</FieldLabel>
            <FieldContent>
              <CategorySelect name="category" />
            </FieldContent>
          </Field>
        </AppForm>
      </CardContent>
    </Card>
  );
}
