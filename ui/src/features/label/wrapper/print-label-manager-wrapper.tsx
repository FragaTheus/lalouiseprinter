"use client";

import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { useParams } from "next/navigation";
import { useState } from "react";
import { LabelStorageSelect } from "../components/label-storage-select";
import AppLookupModal from "@/shared/components/app/app-lookup-modal";
import { useProductListInfinite } from "@/features/product/hook/use-product";
import { useSectorListInfinite } from "@/features/sector/hook/use-sector";
import { Input } from "@/shared/components/ui/input";
import { usePrintLabelByInputSector } from "../hook/use-label";
import { useFormContext, useWatch } from "react-hook-form";
import LabelStorageSelectWrapper from "./label-storage-select-wrapper";

export default function PrintLabelManagerWrapper() {
  const { id: restaurantId } = useParams<{
    id: string;
  }>();

  const { mutate, isPending } = usePrintLabelByInputSector(restaurantId);

  const [productTerm, setProductTerm] = useState<string | undefined>(undefined);
  const [sectorTerm, setSectorTerm] = useState<string | undefined>(undefined);

  const {
    data: productData,
    fetchNextPage: fetchNextProductPage,
    hasNextPage: hasProductNextPage,
    isFetchingNextPage: isFetchingProductNextPage,
  } = useProductListInfinite(restaurantId, { term: productTerm, active: true });

  const {
    data: sectorData,
    fetchNextPage: fetchNextSectorPage,
    hasNextPage: hasSectorNextPage,
    isFetchingNextPage: isFetchingSectorNextPage,
  } = useSectorListInfinite(restaurantId, { term: sectorTerm, active: true });

  const productOptions = productData?.pages.map((page) =>
    page.content.map((product) => ({
      restaurantId: product.id,
      name: product.name,
    })),
  );

  const sectorOptions = sectorData?.pages.map((page) =>
    page.content.map((sector) => ({
      restaurantId: sector.id,
      name: sector.name,
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
      <AppLookupModal
        label="Setor"
        name="sectorId"
        placeholder="Selecione um setor"
        options={sectorOptions}
        onSearch={setSectorTerm}
        fetchNextPage={fetchNextSectorPage}
        hasNextPage={hasSectorNextPage}
        isFetchingNextPage={isFetchingSectorNextPage}
      />
      <LabelStorageSelectWrapper restaurantId={restaurantId} />
      <Field>
        <FieldLabel>Quantidade</FieldLabel>
        <FieldContent>
          <Input name="copies" type="number" />
        </FieldContent>
      </Field>
    </AppForm>
  );
}
