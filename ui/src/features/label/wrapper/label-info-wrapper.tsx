"use client";

import { useLabelInfo, STATUS_LABELS, STATUS_COLORS } from "../hook/use-label";
import { useParams } from "next/navigation";
import LabelInfoLayout from "../layout/label-info-layout";
import { useUserStore } from "@/store/user-store";

export default function LabelInfoWrapper() {
  const { user } = useUserStore();
  const { id: restaurantId, labelId } = useParams<{
    id: string;
    labelId: string;
  }>();

  const { data, isLoading, isError } = useLabelInfo(restaurantId, labelId);

  const items = [
    { label: "Identificador:", children: data?.id ?? "N/A" },
    { label: "Restaurante:", children: data?.restaurantName ?? "N/A" },
    { label: "Setor:", children: data?.sectorName ?? "N/A" },
    { label: "Produto:", children: data?.productName ?? "N/A" },
    { label: "Impresso por:", children: data?.printedBy ?? "N/A" },
    { label: "Lote:", children: data?.lot ?? "N/A" },
    {
      label: "Validade:",
      children: data?.validateDate
        ? new Date(data.validateDate).toLocaleDateString("pt-BR")
        : "N/A",
    },
    {
      label: "Status:",
      children: data?.status ? (
        <span className="flex items-center gap-2">
          <div
            className={`size-3 rounded-full ${STATUS_COLORS[data.status]}`}
          />
          {STATUS_LABELS[data.status]}
        </span>
      ) : (
        "N/A"
      ),
    },
    {
      label: "Criado em:",
      children: data?.createdAt
        ? new Date(data.createdAt).toLocaleDateString("pt-BR")
        : "N/A",
    },
    {
      label: "Atualizado em:",
      children: data?.updateAt
        ? new Date(data.updateAt).toLocaleDateString("pt-BR")
        : "N/A",
    },
  ];

  return (
    <LabelInfoLayout
      title={data?.productName ?? "Etiqueta"}
      isLoading={isLoading}
      isError={isError}
      items={items}
      role={user?.role}
    />
  );
}
