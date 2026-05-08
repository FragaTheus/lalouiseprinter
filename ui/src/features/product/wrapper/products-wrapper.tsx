"use client";

import AppListLayout from "@/shared/components/layouts/app-list-layout";
import { useProductListInfinite } from "../hook/use-product";
import { AppSummaryCardProps } from "@/shared/components/app/app-summary-card";
import {
  ToggleGroup,
  ToggleGroupItem,
} from "@/shared/components/ui/toggle-group";
import { useEffect, useRef } from "react";
import { useDebouncedCallback } from "use-debounce";
import { useParams, useRouter, useSearchParams } from "next/navigation";
import { useUserStore } from "@/store/user-store";

export default function ProductsWrapper() {
  const { user } = useUserStore();
  const { id: restaurantId } = useParams<{ id: string }>();
  const base = `/dashboard/restaurants/${restaurantId}/resources/products`;
  const router = useRouter();
  const searchParams = useSearchParams();
  const term = searchParams.get("term") ?? "";
  const activeParam = searchParams.get("active");
  const active =
    activeParam === "true" ? true : activeParam === "false" ? false : undefined;

  const { data, isLoading, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useProductListInfinite(restaurantId, { term: term || undefined, active });

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
    if (value) {
      params.set("term", value);
    } else {
      params.delete("term");
    }
    router.replace(`?${params.toString()}`);
  }, 500);

  const handleActiveChange = (value: boolean | undefined) => {
    const params = new URLSearchParams(searchParams.toString());
    if (value === undefined) {
      params.delete("active");
    } else {
      params.set("active", String(value));
    }
    router.replace(`?${params.toString()}`);
  };

  const cards: AppSummaryCardProps[] =
    data?.pages.flatMap((p) =>
      p.content.map((product) => ({
        href: `${base}/${product.id}`,
        fields: [
          { label: "Nome", children: <span>{product.name}</span> },
          { label: "Descrição", children: <span>{product.description}</span> },
          {
            label: "Status",
            children: (
              <span
                className={`size-4 rounded-full ${product.active ? "bg-green-300" : "bg-destructive"}`}
              />
            ),
          },
        ],
      })),
    ) ?? [];

  return (
    <AppListLayout
      roles={["MANAGER"]}
      currentRole={user?.role}
      titleLabel="SISTEMA DE GESTÃO"
      title="Gestão de Produtos"
      titleDescription="Controle centralizado dos produtos cadastrados no restaurante. Gerencie informações e disponibilidade dos itens."
      href={`${base}/register`}
      registerText="Cadastrar Produto"
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
            value={
              active === true ? "true" : active === false ? "false" : "all"
            }
            onValueChange={(val) => {
              if (!val) return;
              if (val === "true") handleActiveChange(true);
              else if (val === "false") handleActiveChange(false);
              else handleActiveChange(undefined);
            }}
          >
            <ToggleGroupItem value="all">Todos</ToggleGroupItem>
            <ToggleGroupItem value="true">Ativos</ToggleGroupItem>
            <ToggleGroupItem value="false">Inativos</ToggleGroupItem>
          </ToggleGroup>
        ),
      }}
    />
  );
}
