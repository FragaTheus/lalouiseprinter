"use client";

import { useEffect, useRef, useState } from "react";
import { useParams } from "next/navigation";
import { useDebouncedCallback } from "use-debounce";

import {
  useSearchLabelsByLot,
  STATUS_LABELS,
  STATUS_COLORS,
} from "../hook/use-label";
import { AppSummaryCardProps } from "@/shared/components/app/app-summary-card";
import AppListLayout from "@/shared/components/layouts/app-list-layout";
import { useUserStore } from "@/store/user-store";

export default function LabelsLotWrapper() {
  const { user } = useUserStore();
  const { id: restaurantId } = useParams<{ id: string }>();

  const base = `/dashboard/restaurants/${restaurantId}/resources/labels`;

  const [lotCode, setLotCode] = useState("");

  const { data, isLoading, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useSearchLabelsByLot(restaurantId, lotCode);

  const sentinelRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasNextPage && !isFetchingNextPage) {
          fetchNextPage();
        }
      },
      { threshold: 1.0 },
    );
    if (sentinelRef.current) observer.observe(sentinelRef.current);
    return () => observer.disconnect();
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  const handleTermChange = useDebouncedCallback((value: string) => {
    setLotCode(value);
  }, 3000);

  const cards: AppSummaryCardProps[] =
    data?.pages.flatMap((p) =>
      p.content.map((label) => ({
        href: `${base}/${label.id}`,
        fields: [
          { label: "Produto", children: <span>{label.productName}</span> },
          { label: "Setor", children: <span>{label.sectorName}</span> },
          { label: "Lote", children: <span>{label.lot}</span> },
          {
            label: "Validade",
            children: (
              <span>
                {label.validateDate
                  ? new Date(label.validateDate).toLocaleDateString("pt-BR")
                  : "N/A"}
              </span>
            ),
          },
          {
            label: "Status",
            children: (
              <span className="flex items-center gap-2">
                <div
                  className={`size-3 rounded-full ${STATUS_COLORS[label.status]}`}
                />
                {STATUS_LABELS[label.status]}
              </span>
            ),
          },
        ],
      })),
    ) ?? [];

  return (
    <AppListLayout
      roles={["ADMIN", "MANAGER"]}
      currentRole={user?.role}
      titleLabel="RASTREABILIDADE"
      title="Rastreio por Lote"
      titleDescription="Insira um numero de um lote para visualizar todas as etiquetas associadas a ele."
      href=""
      registerText="Registrar nova etiqueta"
      cards={cards}
      isLoading={isLoading}
      sentinelRef={sentinelRef}
      isListLoading={isFetchingNextPage}
      filterProps={{
        term: lotCode,
        onTermChange: handleTermChange,
        placeholder: "Código do lote...",
      }}
    />
  );
}
