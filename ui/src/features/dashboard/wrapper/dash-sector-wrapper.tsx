"use client";

import AppRouteGuard from "@/shared/components/app/app-route-guard";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import { useUserStore } from "@/store/user-store";
import DashboardTitle from "../components/dash-title";
import AppDashboardLayout from "@/shared/components/layouts/app-dashboard-layout";
import { DashCardLayoutProps } from "../layout/dash-card-layout";
import { BiInfoCircle, BiLabel, BiPlus } from "react-icons/bi";
import { useParams } from "next/navigation";

export default function DashSectorWrapper() {
  const { user } = useUserStore();
  const { sectorId, id } = useParams<{
    sectorId: string;
    id: string;
  }>();

  const cards = [
    {
      className: "md:col-span-2",
      href: "/dashboard/admins",
      Icon: BiLabel,
      title: "Etiquetas",
      description:
        "Gerencie perfis de alta hierarquia, permissões globais do sistema e logs de segurança crítica.",
    },
    {
      href: "",
      Icon: BiPlus,
      title: "Nova etiqueta",
      description:
        "Gere uma nova etiqueta, defina suas características e monitore logs de segurança crítica.",
    },
    {
      roles: ["ADMIN", "MANAGER"],
      href: `/dashboard/restaurants/${id}/resources/sectors/${sectorId}/info`,
      Icon: BiInfoCircle,
      title: "Informações do setor",
      description:
        "Visualize informações detalhadas do setor, incluindo dados de desempenho, histórico de inspeções e conformidade regulatória.",
    },
  ] satisfies DashCardLayoutProps[];

  return (
    <AppRouteGuard
      allowedRoles={["ADMIN", "MANAGER", "STAFF"]}
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
