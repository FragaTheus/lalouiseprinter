"use client";

import { useStaffInfo } from "../hooks/use-staff";
import { useParams } from "next/navigation";
import StaffInfoLayout from "../layout/staff-info-layout";

export default function StaffInfoWrapper() {
  const { id: restaurantId, staffId } = useParams<{
    id: string;
    sectorId: string;
    staffId: string;
  }>();
  const { data, isLoading, isError } = useStaffInfo(restaurantId, staffId);

  const items = [
    { label: "Identificador:", children: data?.id ?? "N/A" },
    { label: "Nome:", children: data?.nickname ?? "N/A" },
    { label: "Email:", children: data?.email ?? "N/A" },
    { label: "Restaurante:", children: data?.restaurantName ?? "N/A" },
    { label: "Setor:", children: data?.sectorName ?? "N/A" },
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
    <StaffInfoLayout
      title={data?.nickname ?? "N/A"}
      isLoading={isLoading}
      isError={isError}
      items={items}
      isActive={data?.active ?? false}
    />
  );
}
