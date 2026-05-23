"use client";

import { RiAdminFill } from "react-icons/ri";
import { DashCardLayoutProps } from "../layout/dash-card-layout";
import { MdRestaurant } from "react-icons/md";
import AppDashboardLayout from "@/shared/components/layouts/app-dashboard-layout";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import DashboardTitle from "../components/dash-title";
import AppRouteGuard from "@/shared/components/app/app-route-guard";
import { useUserStore } from "@/store/user-store";
import { BiPlus } from "react-icons/bi";

const cards = [
  {
    roles: ["ADMIN"],
    className: "md:col-span-2",
    href: "/dashboard/admins",
    Icon: RiAdminFill,
    title: "Administradores",
    description: "Gerencie os administradores do sistema.",
  },
  {
    roles: ["ADMIN"],
    href: "/dashboard/admins/register",
    Icon: BiPlus,
    title: "Registrar Administrador",
    description:
      "Crie perfis administrativos para colaborarem com a gestão do sistema.",
  },
  {
    roles: ["ADMIN"],
    href: "/dashboard/restaurants/register",
    Icon: BiPlus,
    title: "Registrar Restaurante",
    description:
      "Registre uma nova unidade parceira para expandir a rede de estabelecimentos do sistema.",
  },
  {
    className: "md:col-span-2",
    roles: ["ADMIN"],
    href: "/dashboard/restaurants",
    Icon: MdRestaurant,
    title: "Restaurantes",
    description:
      "Controle de unidades, licenças sanitárias e perfis de estabelecimentos parceiros.",
  },
] satisfies DashCardLayoutProps[];

export default function DashboardWrapper() {
  const { user } = useUserStore();
  return (
    <AppRouteGuard
      allowedRoles={["ADMIN"]}
      forbiddenPath={`/dashboard/restaurants/${user?.restaurantId}/resources`}
    >
      <AppPageLayout>
        <DashboardTitle
          label="SISTEMA DE GESTÃO DE ETIQUETAS"
          title={"Painel\n Administrativo"}
          description={`Bem vindo de volta, ${user?.nickname}!\n Gerencie recursos do sistema ou de restaurantes.`}
        />
        <AppDashboardLayout cards={cards} />
      </AppPageLayout>
    </AppRouteGuard>
  );
}
