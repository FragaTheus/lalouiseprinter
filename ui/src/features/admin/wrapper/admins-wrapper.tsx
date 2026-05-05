"use client";

import { AdminSummaryCardProps } from "../component/admin-summary-card";
import { useAdminListInfinite } from "../hook/use-admin";
import AdminsLayout from "../layout/admins-layout";

export default function AdminsWrapper() {
  const { data, isLoading } = useAdminListInfinite();

  const cards: AdminSummaryCardProps[] =
    data?.pages.flatMap((p) =>
      p.content.map((adm) => ({
        href: `/dashboard/admins/${adm.id}`,
        fields: [
          { label: "Nome", children: <span>{adm.nickname}</span> },
          { label: "Email", children: <span>{adm.email}</span> },
          {
            label: "Status",
            children: (
              <span
                className={`size-4 rounded-full ${adm.active ? "bg-green-300" : "bg-red-300"}`}
              />
            ),
          },
        ],
      })),
    ) ?? [];

  return <AdminsLayout cards={cards} isLoading={isLoading} />;
}
