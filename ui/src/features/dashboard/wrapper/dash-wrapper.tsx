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
    description:
      "Gerencie perfis de alta hierarquia, permissões globais do sistema e logs de segurança crítica.",
  },
  {
    roles: ["ADMIN"],
    href: "/dashboard/admins/register",
    Icon: BiPlus,
    title: "Registrar Administrador",
    description:
      "Crie perfis de alta hierarquia, defina permissões globais do sistema e monitore logs de segurança crítica.",
  },
  {
    roles: ["ADMIN"],
    href: "/dashboard/restaurants/register",
    Icon: BiPlus,
    title: "Registrar Restaurante",
    description:
      "Registre novas unidades, gerencie licenças sanitárias e configure perfis de estabelecimentos parceiros para expandir sua rede de gestão higiênica.",
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
          label="SISTEMA DE GESTÃO SANITÁRIAs"
          title="Central de Gestão"
          description="Bem-vindo à Lalouise. Monitore padrões, gerencie acessos e assegure a excelência higiênica em todos os pontos de operação com nossa interface de alta precisão."
        />
        <AppDashboardLayout cards={cards} />
      </AppPageLayout>
    </AppRouteGuard>
  );
}
