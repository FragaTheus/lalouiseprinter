"use client";

import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import { useUserStore } from "@/store/user-store";
import DashboardTitle from "../components/dash-title";
import AppDashboardLayout from "@/shared/components/layouts/app-dashboard-layout";
import { DashCardLayoutProps } from "../layout/dash-card-layout";
import { BiInfoCircle, BiPlus, BiSearch } from "react-icons/bi";
import { useParams } from "next/navigation";

export default function DashSectorWrapper() {
  const { user } = useUserStore();
  const { sectorId, id } = useParams<{
    sectorId: string;
    id: string;
  }>();

  const cards = [
    {
      href: `/dashboard/restaurants/${id}/resources/sectors/${sectorId}/resources/labels/sector`,
      Icon: BiSearch,
      title: "Etiquetas do Setor",
      description:
        "Filtre e consulte as etiquetas diretamente pelo contexto deste setor.",
    },
    {
      roles: ["MANAGER", "STAFF"],
      href: `/dashboard/restaurants/${id}/resources/sectors/${sectorId}/resources/labels/print`,
      Icon: BiPlus,
      title: "Nova etiqueta",
      description: "Imprima uma nova etiqueta para um produto neste setor.",
    },
    {
      href: `/dashboard/restaurants/${id}/resources/sectors/${sectorId}/info`,
      Icon: BiInfoCircle,
      title: "Informações do setor",
      description:
        "Visualize informações detalhadas do setor, incluindo dados de desempenho, histórico de inspeções e conformidade regulatória.",
    },
  ] satisfies DashCardLayoutProps[];

  return (
    <AppPageLayout>
      <DashboardTitle
        label="SISTEMA DE GESTÃO SANITÁRIAs"
        title="Central de Gestão"
        description="Bem-vindo à Lalouise. Monitore padrões, gerencie acessos e assegure a excelência higiênica em todos os pontos de operação com nossa interface de alta precisão."
      />
      <AppDashboardLayout cards={cards} />
    </AppPageLayout>
  );
}
