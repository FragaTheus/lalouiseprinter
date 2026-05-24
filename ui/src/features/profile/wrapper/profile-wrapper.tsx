"use client";

import { useUserStore } from "@/store/user-store";
import { PerfilItem } from "../../../shared/components/app/app-info-card";
import { useProfile } from "../hooks/use-profile";
import ProfileLayout from "@/features/profile/layout/profile-layout";

export default function ProfileWrapper() {
  const { data, isLoading, isError } = useProfile();
  const { user } = useUserStore();
  const items = [
    {
      label: "identificador:",
      children: data?.id || "N/A",
    },
    {
      label: "nome:",
      children: data?.nickname || "N/A",
    },
    {
      label: "email:",
      children: data?.email || "N/A",
    },
    ...(data?.restaurantName
      ? [
          {
            label: "restaurante:",
            children: data.restaurantName,
          },
        ]
      : []),
    {
      label: "conosco desde:",
      children: data?.createdAt
        ? new Date(data.createdAt).toLocaleDateString()
        : "N/A",
    },
  ] satisfies PerfilItem[];

  return (
    <ProfileLayout
      isError={isError}
      role={user?.role || ""}
      title={data?.nickname || "Nome"}
      items={items}
      isLoading={isLoading}
      roles={["ADMIN", "MANAGER"]}
      userRole={user?.role || "N/A"}
    />
  );
}
