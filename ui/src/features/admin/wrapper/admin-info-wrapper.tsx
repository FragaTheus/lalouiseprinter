"use client";

import { useAdminInfo } from "../hook/use-admin";
import { useParams } from "next/navigation";
import AdminInfoLayout from "../layout/admin-info-layout";

export default function AdminInfoWrapper() {
  const { id } = useParams<{ id: string }>();
  const { data, isLoading, isError } = useAdminInfo(id);

  const items = [
    { label: "Identificador:", children: data?.id ?? "N/A" },
    { label: "Nome:", children: data?.nickname ?? "N/A" },
    { label: "Email:", children: data?.email ?? "N/A" },
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
    <AdminInfoLayout
      title={data?.nickname ?? "N/A"}
      isLoading={isLoading}
      isError={isError}
      items={items}
    />
  );
}
