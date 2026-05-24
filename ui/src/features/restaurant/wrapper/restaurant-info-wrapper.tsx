"use client";

import { useRestaurantInfo } from "../hooks/use-restaurant";
import { useParams } from "next/navigation";
import RestaurantInfoLayout from "../layout/restaurant-info-layout";
import { useUserStore } from "@/store/user-store";

export default function RestaurantInfoWrapper() {
  const { id } = useParams<{ id: string }>();
  const { data, isLoading, isError } = useRestaurantInfo(id);
  const { user } = useUserStore();

  const items = [
    { label: "Identificador:", children: data?.id ?? "N/A" },
    { label: "Nome:", children: data?.name ?? "N/A" },
    { label: "CNPJ:", children: data?.cnpj ?? "N/A" },
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
    <RestaurantInfoLayout
      roles={["MANAGER", "ADMIN"]}
      userRole={user?.role ?? "N/A"}
      title={data?.name ?? "N/A"}
      isLoading={isLoading}
      isError={isError}
      items={items}
      isActive={data?.active ?? false}
      role={user?.role ?? undefined}
    />
  );
}
