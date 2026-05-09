"use client";

import { useSectorInfo, STORAGE_LABELS } from "../hook/use-sector";
import { useParams } from "next/navigation";
import SectorInfoLayout from "../layout/sector-info-layout";
import { useRouteGuard } from "@/shared/components/app/app-route-guard";

export default function SectorInfoWrapper() {
  const { sectorId, id: restaurantId } = useParams<{
    sectorId: string;
    id: string;
  }>();
  const { role } = useRouteGuard();
  const { data, isLoading, isError } = useSectorInfo(restaurantId, sectorId);

  const storagesLabel =
    data?.storages?.map((s) => STORAGE_LABELS[s] ?? s).join(", ") ?? "N/A";

  const items = [
    { label: "Identificador:", children: data?.id ?? "N/A" },
    { label: "Nome:", children: data?.name ?? "N/A" },
    { label: "Descrição:", children: data?.description ?? "N/A" },
    { label: "Armazenamentos:", children: storagesLabel },
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
    // TODO: implementar vínculo de responsável futuramente
    // { label: "Responsável:", children: data?.responsibleId ?? "N/A" },
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
    <SectorInfoLayout
      title={data?.name ?? "N/A"}
      isLoading={isLoading}
      isError={isError}
      items={items}
      isActive={data?.active ?? false}
      role={role ?? undefined}
    />
  );
}
