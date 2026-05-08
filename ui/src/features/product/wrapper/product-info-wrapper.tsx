"use client";

import { useProductInfo } from "../hook/use-product";
import { useParams } from "next/navigation";
import ProductInfoLayout from "../layout/product-info-layout";

export default function ProductInfoWrapper() {
  const { productId, id: restaurantId } = useParams<{
    productId: string;
    id: string;
  }>();
  const { data, isLoading, isError } = useProductInfo(restaurantId, productId);

  const items = [
    { label: "Identificador:", children: data?.id ?? "N/A" },
    { label: "Nome:", children: data?.name ?? "N/A" },
    { label: "Descrição:", children: data?.description ?? "N/A" },
    {
      label: "Status:",
      children: (
        <span className="flex items-center gap-2">
          <div
            className={`size-3 ${data?.active ? "bg-green-500" : "bg-red-500"} rounded-full`}
          />
          {data?.active ? "Ativo" : "Inativo"}
        </span>
      ),
    },
    {
      label: "Criado em:",
      children: data?.createdAt
        ? new Date(data.createdAt).toLocaleDateString()
        : "N/A",
    },
    {
      label: "Atualizado em:",
      children: data?.updatedAt
        ? new Date(data.updatedAt).toLocaleDateString()
        : "N/A",
    },
  ];

  return (
    <ProductInfoLayout
      title={data?.name ?? "N/A"}
      isLoading={isLoading}
      isError={isError}
      items={items}
      isActive={data?.active ?? false}
    />
  );
}
