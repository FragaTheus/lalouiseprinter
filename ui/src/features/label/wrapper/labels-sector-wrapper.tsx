"use client";

import AppListLayout from "@/shared/components/layouts/app-list-layout";
import {
  useLabelListInfinite,
  LabelStatus,
  STATUS_LABELS,
  STATUS_COLORS,
} from "../hook/use-label";
import { AppSummaryCardProps } from "@/shared/components/app/app-summary-card";
import {
  ToggleGroup,
  ToggleGroupItem,
} from "@/shared/components/ui/toggle-group";
import { useEffect, useRef } from "react";
import { useDebouncedCallback } from "use-debounce";
import { useParams, useRouter, useSearchParams } from "next/navigation";
import { useUserStore } from "@/store/user-store";

export default function LabelsSectorWrapper() {
  const { user } = useUserStore();
  const { id: restaurantId, sectorId } = useParams<{
    id: string;
    sectorId: string;
  }>();

  const base = `/dashboard/restaurants/${restaurantId}/resources`;
  const router = useRouter();
  const searchParams = useSearchParams();
  const term = searchParams.get("term") ?? "";
  const statusParam = searchParams.get("status") as LabelStatus | null;

  const { data, isLoading, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useLabelListInfinite(restaurantId, sectorId, {
      term: term || undefined,
      status: statusParam ?? undefined,
    });

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
    const params = new URLSearchParams(searchParams.toString());
    if (value) params.set("term", value);
    else params.delete("term");
    router.replace(`?${params.toString()}`);
  }, 500);

  const handleStatusChange = (value: LabelStatus | "all") => {
    const params = new URLSearchParams(searchParams.toString());
    if (value === "all") params.delete("status");
    else params.set("status", value);
    router.replace(`?${params.toString()}`);
  };

  const STATUS_OPTIONS: LabelStatus[] = [
    "ACTIVE",
    "EXPIRING",
    "EXPIRED",
    "DISCARDED",
  ];

  const cards: AppSummaryCardProps[] =
    data?.pages.flatMap((p) =>
      p.content.map((label) => ({
        href: `${base}/labels/${label.id}`,
        fields: [
          { label: "Produto", children: <span>{label.productName}</span> },
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
      roles={["MANAGER", "STAFF"]}
      currentRole={user?.role}
      titleLabel="SISTEMA DE GESTÃO"
      title="Etiquetas do Setor"
      titleDescription="Gerencie e monitore as etiquetas emitidas neste setor."
      href={`${base}/sectors/${sectorId}/resources/labels/print`}
      registerText="Nova Etiqueta"
      cards={cards}
      isLoading={isLoading}
      sentinelRef={sentinelRef}
      isListLoading={isFetchingNextPage}
      filterProps={{
        term,
        onTermChange: handleTermChange,
        children: (
          <ToggleGroup
            type="single"
            variant="outline"
            value={statusParam ?? "all"}
            onValueChange={(val) => {
              if (!val) return;
              handleStatusChange(val as LabelStatus | "all");
            }}
          >
            <ToggleGroupItem value="all">Todos</ToggleGroupItem>
            {STATUS_OPTIONS.map((s) => (
              <ToggleGroupItem key={s} value={s}>
                {STATUS_LABELS[s]}
              </ToggleGroupItem>
            ))}
          </ToggleGroup>
        ),
      }}
    />
  );
}
