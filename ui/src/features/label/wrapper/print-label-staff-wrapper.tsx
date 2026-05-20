"use client";

import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { useParams } from "next/navigation";
import { useState } from "react";
import { LabelStorageSelect } from "../components/label-storage-select";
import AppLookupModal from "@/shared/components/app/app-lookup-modal";
import { useProductListInfinite } from "@/features/product/hook/use-product";
import { usePrintLabel } from "../hook/use-label";

export default function PrintLabelStaffWrapper() {
  const { id: restaurantId, sectorId } = useParams<{
    id: string;
    sectorId: string;
  }>();
  const { mutate, isPending } = usePrintLabel(restaurantId, sectorId);

  const [productTerm, setProductTerm] = useState<string | undefined>(undefined);

  const {
    data: productData,
    fetchNextPage: fetchNextProductPage,
    hasNextPage: hasProductNextPage,
    isFetchingNextPage: isFetchingProductNextPage,
  } = useProductListInfinite(restaurantId, { term: productTerm, active: true });

  const productOptions = productData?.pages.map((page) =>
    page.content.map((product) => ({
      restaurantId: product.id,
      name: product.name,
    })),
  );

  return (
    <AppForm
      btnText="Imprimir Etiqueta"
      onSubmit={mutate}
      isPending={isPending}
    >
      <AppLookupModal
        label="Produto"
        name="productId"
        placeholder="Selecione um produto"
        options={productOptions}
        onSearch={setProductTerm}
        fetchNextPage={fetchNextProductPage}
        hasNextPage={hasProductNextPage}
        isFetchingNextPage={isFetchingProductNextPage}
      />
      <Field>
        <FieldLabel>Armazenamento</FieldLabel>
        <FieldContent>
          <LabelStorageSelect
            name="storage"
            sectorId={sectorId}
            restaurantId={restaurantId}
          />
        </FieldContent>
      </Field>
    </AppForm>
  );
}
