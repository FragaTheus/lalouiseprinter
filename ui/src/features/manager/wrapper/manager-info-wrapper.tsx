"use client";

import { useManagerInfo } from "../hooks/use-manager";
import { useParams } from "next/navigation";
import { useUserStore } from "@/store/user-store";
import ManagerInfoLayout from "../layout/manager-info-layout";

export default function ManagerInfoWrapper() {
  const { managerId } = useParams<{ managerId: string; id: string }>();
  const { id: restaurantId } = useParams<{ id: string }>();
  const { data, isLoading, isError } = useManagerInfo(restaurantId, managerId);
  const { user } = useUserStore();

  const isProfile = user?.id === data?.id;

  const items = [
    { label: "Identificador:", children: data?.id ?? "N/A" },
    { label: "Nome:", children: data?.nickname ?? "N/A" },
    { label: "Email:", children: data?.email ?? "N/A" },
    { label: "Restaurante:", children: data?.restaurantName ?? "N/A" },
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
    <ManagerInfoLayout
      title={data?.nickname ?? "N/A"}
      isLoading={isLoading}
      isError={isError}
      items={items}
      isActive={data?.active ?? false}
      isProfile={isProfile}
    />
  );
}
