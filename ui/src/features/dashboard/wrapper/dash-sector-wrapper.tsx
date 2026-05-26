"use client";

import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import DashboardTitle from "../components/dash-title";
import AppDashboardLayout from "@/shared/components/layouts/app-dashboard-layout";
import { DashCardLayoutProps } from "../layout/dash-card-layout";
import { BiInfoCircle, BiPlus, BiSearch } from "react-icons/bi";
import { useParams } from "next/navigation";
import { useSectorLookup } from "@/features/sector/hook/use-sector";

export default function DashSectorWrapper() {
  const { sectorId, id } = useParams<{
    sectorId: string;
    id: string;
  }>();

  const { data } = useSectorLookup(id, sectorId);

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
        label="GESTÃO DE SETORES"
        title={data?.sectorName || "Central de Gestão"}
        description="Gerencie os setores do seu restaurante, visualize informações detalhadas e acompanhe o desempenho de cada unidade."
      />
      <AppDashboardLayout cards={cards} />
    </AppPageLayout>
  );
}
