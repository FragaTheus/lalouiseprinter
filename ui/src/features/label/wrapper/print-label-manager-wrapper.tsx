"use client";

import AppForm from "@/shared/components/app/app-form";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { useParams } from "next/navigation";
import { useState } from "react";
import { usePrintLabel } from "../hook/use-label";
import { LabelStorageSelect } from "../components/label-storage-select";
import AppLookupModal from "@/shared/components/app/app-lookup-modal";
import { useProductListInfinite } from "@/features/product/hook/use-product";
import { useSectorListInfinite } from "@/features/sector/hook/use-sector";

export default function PrintLabelManagerWrapper() {
  const { id: restaurantId, sectorId } = useParams<{
    id: string;
    sectorId: string;
  }>();

  const { mutate, isPending } = usePrintLabel(restaurantId, sectorId);

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

  const handleSubmit = (data: Record<string, string>) => {
    mutate({
      productId: data.productId,
      sectorId: data.sectorId ?? sectorId,
      storage: data.storage as never,
    });
  };

  return (
    <AppForm
      btnText="Imprimir Etiqueta"
      onSubmit={handleSubmit}
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
      <Field>
        <FieldLabel>Armazenamento</FieldLabel>
        <FieldContent>
          <LabelStorageSelect name="storage" />
        </FieldContent>
      </Field>
    </AppForm>
  );
}
