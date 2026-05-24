"use client";

import { PerfilItem } from "@/shared/components/app/app-info-card";
import AppInfoLayout from "@/shared/components/layouts/app-info-layout";
import RestaurantChangeNameCard from "../components/restaurant-change-name-card";
import AppToggleActivation from "@/shared/components/app/app-toggle-activation";
import {
  useDeleteRestaurant,
  useReactiveRestaurant,
} from "../hooks/use-restaurant";
import { useParams } from "next/navigation";

export default function RestaurantInfoLayout({
  title,
  isLoading,
  isError,
  items,
  isActive,
  roles,
  userRole,
}: {
  title: string;
  isLoading: boolean;
  isError: boolean;
  items: PerfilItem[];
  role: string | undefined;
  isActive: boolean;
  roles: string[];
  userRole: string;
}) {
  const { id } = useParams<{ id: string }>();
  const { mutate: deactivate } = useDeleteRestaurant(id);
  const { mutate: reactive } = useReactiveRestaurant(id);

  return (
    <AppInfoLayout
      description="painel do restaurante"
      items={items}
      title={title}
      isError={isError}
      isLoading={isLoading}
      roles={roles}
      userRole={userRole}
    >
      <h2 className="font-semibold text-xl">Configurações do restaurante</h2>
      <RestaurantChangeNameCard />
      <AppToggleActivation
        isActive={isActive}
        active={{
          text: "Desativar restaurante",
          mutate: deactivate,
        }}
        inactive={{
          text: "Reativar restaurante",
          mutate: reactive,
        }}
      />
    </AppInfoLayout>
  );
}
