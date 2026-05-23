"use client";

import AppDashboardLayout from "@/shared/components/layouts/app-dashboard-layout";
import { DashCardLayoutProps } from "../layout/dash-card-layout";
import { MdDashboard, MdInfo } from "react-icons/md";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import DashboardTitle from "../components/dash-title";
import { useParams } from "next/navigation";
import { BiLabel, BiPlus } from "react-icons/bi";
import { useRestaurantLookUpInfo } from "@/features/restaurant/hooks/use-restaurant";
import AppRouteGuard from "@/shared/components/app/app-route-guard";
import { useUserStore } from "@/store/user-store";
import { HiUsers } from "react-icons/hi";
import { CgProductHunt } from "react-icons/cg";

export default function DashRestaurantWrapper() {
  const { id } = useParams<{ id: string }>();
  const { data } = useRestaurantLookUpInfo(id);
  const { user } = useUserStore();

  const base = `/dashboard/restaurants/${id}`;

  const cards = [
    {
      href: `${base}/resources/labels`,
      Icon: BiLabel,
      title: "Etiquetas",
      description:
        "Imprima ou gerencie etiquetas de controle de validade e rastreabilidade.",
    },
    {
      className: "lg:col-span-2",
      href: `${base}/resources/staffs`,
      Icon: HiUsers,
      title: "Colaboradores",
      description: "Gerenciar colaboradores e gerentes do restaurante.",
    },
    {
      className: "lg:col-span-2",
      href: `${base}/resources/sectors`,
      Icon: MdDashboard,
      title: "Setores",
      description:
        "Gerencie setores, armazenamentos para controle de qualidade alimentar.",
    },
    {
      href: `${base}/resources/sectors/register`,
      Icon: BiPlus,
      title: "Registrar setor",
      description: "Cadastrar novo setor para a sua unidade.",
    },
    {
      href: `${base}/resources/products`,
      Icon: CgProductHunt,
      title: "Produtos",
      description: "Gerencie os produtos utilizados na unidade",
    },
    {
      href: `${base}/resources/products/register`,
      Icon: BiPlus,
      title: "Registrar produto",
      description: "Cadastrar novo produto para a sua unidade.",
    },
    {
      href: `${base}/info`,
      Icon: MdInfo,
      title: "Informações do restaurante",
      description: "Dados cadastrais, CNPJ e status do restaurante.",
    },
  ] satisfies DashCardLayoutProps[];

  return (
    <AppRouteGuard
      allowedRoles={["MANAGER", "ADMIN"]}
      forbiddenPath={`/dashboard/restaurants/${id}/resources/sectors/${user?.sectorId}/resources`}
    >
      <AppPageLayout>
        <DashboardTitle
          label="SISTEMA DE GESTÃO SANITÁRIA"
          title={data?.restaurantName ?? "Restaurante"}
          description={`Bem vindo de volta, ${user?.nickname}! \n Acesse e gerencie os recursos do seu restaurante.`}
        />
        <AppDashboardLayout cards={cards} />
      </AppPageLayout>
    </AppRouteGuard>
  );
}
