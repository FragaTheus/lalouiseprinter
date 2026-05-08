"use client";

import AppDashboardLayout from "@/shared/components/layouts/app-dashboard-layout";
import { DashCardLayoutProps } from "../layout/dash-card-layout";
import { RiUserShared2Fill } from "react-icons/ri";
import { HiUsers } from "react-icons/hi";
import { MdDashboard, MdInventory, MdLabel, MdInfo } from "react-icons/md";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import DashboardTitle from "../components/dash-title";
import { useParams } from "next/navigation";
import { BiPlus } from "react-icons/bi";
import { useRestaurantLookUpInfo } from "@/features/restaurant/hooks/use-restaurant";

export default function DashRestaurantWrapper() {
  const { id } = useParams<{ id: string }>();
  const base = `/dashboard/restaurants/${id}`;
  const { data } = useRestaurantLookUpInfo(id);

  const cards = [
    {
      className: "lg:col-span-2",
      href: `${base}/resources/managers`,
      Icon: RiUserShared2Fill,
      title: "Gerentes",
      description:
        "Coordenação de responsáveis técnicos e gestores operacionais por unidade.",
    },
    {
      href: `${base}/resources/managers/register`,
      Icon: BiPlus,
      title: "Novo Gerente",
      description:
        "Configure as credenciais de acesso do gerente para este restaurante.",
    },
    {
      href: `${base}/resources/staffs`,
      Icon: HiUsers,
      title: "Colaboradores",
      description:
        "Registro de staff, certificados de treinamento e históricos de conduta sanitária.",
    },
    {
      className: "lg:col-span-2",
      href: `${base}/resources/sectors`,
      Icon: MdDashboard,
      title: "Setores",
      description:
        "Sistema de rotulagem, datas de validade e controle de rastreabilidade de insumos.",
    },
    {
      className: "lg:col-span-2",
      href: `${base}/resources/labels`,
      Icon: MdLabel,
      title: "Etiquetas",
      description:
        "Sistema de rotulagem, datas de validade e controle de rastreabilidade de insumos.",
    },
    {
      href: `${base}/resources/products`,
      Icon: MdInventory,
      title: "Produtos",
      description:
        "Catálogo técnico de ingredientes, fichas de segurança química e especificações de fornecedores.",
    },
    {
      href: `${base}/info`,
      Icon: MdInfo,
      title: "Informações",
      description: "Dados cadastrais, CNPJ e status do restaurante.",
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
