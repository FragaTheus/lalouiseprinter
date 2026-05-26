"use client";

import DashboardTitle from "@/features/dashboard/components/dash-title";
import { DashCardLayoutProps } from "@/features/dashboard/layout/dash-card-layout";
import { useRestaurantLookUpInfo } from "@/features/restaurant/hooks/use-restaurant";
import AppDashboardLayout from "@/shared/components/layouts/app-dashboard-layout";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import { useParams } from "next/navigation";
import { BiPrinter } from "react-icons/bi";
import { MdLabel, MdTimeline } from "react-icons/md";

export default function Labels() {
  const { id } = useParams<{ id: string }>();
  const base = `/dashboard/restaurants/${id}/resources/labels`;
  const { data } = useRestaurantLookUpInfo(id);

  const cards = [
    {
      roles: ["MANAGER", "STAFF"],
      href: `${base}/print`,
      Icon: BiPrinter,
      title: "Imprimir nova etiqueta",
      description: "Imprima uma nova etiqueta para qualquer um dos setores.",
    },
    {
      href: `${base}/list`,
      Icon: MdLabel,
      title: "Etiquetas",
      description:
        "Monitoramento global de etiquetas emitidas pelo restaurante, validades e conformidade sanitária.",
    },
    {
      href: `${base}/lot`,
      Icon: MdTimeline,
      title: "Rastreio",
      description:
        "Rastreie todas as etiquetas associadas a um lote específico do restaurante para entender o fluxo.",
    },
  ] satisfies DashCardLayoutProps[];

  return (
    <AppPageLayout>
      <DashboardTitle
        label="SISTEMA DE GESTÃO SANITÁRIA"
        title={data?.restaurantName ?? "Restaurante"}
        description="Gerencie as etiquetas de controle sanitário para os setores do seu restaurante."
      />
      <AppDashboardLayout cards={cards} />
    </AppPageLayout>
  );
}
