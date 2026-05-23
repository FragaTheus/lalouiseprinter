"use client";

import DashboardTitle from "@/features/dashboard/components/dash-title";
import { DashCardLayoutProps } from "@/features/dashboard/layout/dash-card-layout";
import { useRestaurantLookUpInfo } from "@/features/restaurant/hooks/use-restaurant";
import AppDashboardLayout from "@/shared/components/layouts/app-dashboard-layout";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import { useUserStore } from "@/store/user-store";
import { useParams } from "next/navigation";
import { BiPlus } from "react-icons/bi";
import { HiUsers } from "react-icons/hi";
import { RiUserShared2Fill } from "react-icons/ri";

export default function StaffsPage() {
  const { id } = useParams<{ id: string }>();
  const { data } = useRestaurantLookUpInfo(id);
  const { user } = useUserStore();

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
      href: `${base}/register`,
      Icon: BiPlus,
      title: "Novo colaborador",
      description:
        "Coordenação de colaboradores operacionais para unidade do seu restaurante.",
    },
    {
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
        label="SISTEMA DE GESTÃO SANITÁRIA"
        title={data?.restaurantName ?? "Restaurante"}
        description="Bem-vindo à Lalouise. Monitore padrões, gerencie acessos e assegure a excelência higiênica em todos os pontos de operação com nossa interface de alta precisão."
      />
      <AppDashboardLayout cards={cards} />
    </AppPageLayout>
  );
}
