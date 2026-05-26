"use client";

import DashboardTitle from "@/features/dashboard/components/dash-title";
import { DashCardLayoutProps } from "@/features/dashboard/layout/dash-card-layout";
import { useRestaurantLookUpInfo } from "@/features/restaurant/hooks/use-restaurant";
import AppDashboardLayout from "@/shared/components/layouts/app-dashboard-layout";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import { useParams } from "next/navigation";
import { BiPlus } from "react-icons/bi";
import { HiUsers } from "react-icons/hi";
import { RiUserShared2Fill } from "react-icons/ri";

export default function StaffsPage() {
  const { id } = useParams<{ id: string }>();
  const { data } = useRestaurantLookUpInfo(id);
  const base = `/dashboard/restaurants/${id}/resources/staffs`;

  const cards = [
    {
      className: "lg:col-span-2",
      href: `${base}/list`,
      Icon: HiUsers,
      title: "Colaboradores",
      description:
        "Registro de staff, certificados de treinamento e históricos de conduta sanitária.",
    },
    {
      roles: ["MANAGER"],
      href: `${base}/register`,
      Icon: BiPlus,
      title: "Novo colaborador",
      description:
        "Coordenação de colaboradores operacionais para unidade do seu restaurante.",
    },
    {
      roles: ["MANAGER", "ADMIN"],
      href: `${base}/managers/register`,
      Icon: BiPlus,
      title: "Novo Gerente",
      description:
        "Configure as credenciais de acesso do gerente para este restaurante.",
    },
    {
      className: "lg:col-span-2",
      href: `${base}/managers/list`,
      Icon: RiUserShared2Fill,
      title: "Gerentes",
      description:
        "Coordenação de responsáveis técnicos e gestores operacionais por unidade.",
    },
  ] satisfies DashCardLayoutProps[];

  return (
    <AppPageLayout>
      <DashboardTitle
        label="SISTEMA DE GESTÃO DE COLABORADORES"
        title={data?.restaurantName ?? "Restaurante"}
        description="Gerencie seus colaboradores, cadastre, edite ou avalie as contas dos colaboradores e gerentes do seu restaurante."
      />
      <AppDashboardLayout cards={cards} />
    </AppPageLayout>
  );
}
