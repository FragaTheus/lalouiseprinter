"use client";

import DashboardTitle from "@/features/dashboard/components/dash-title";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import AdminSummaryCard, {
  AdminSummaryCardProps,
} from "../component/admin-summary-card";
import AdminRegisterButton from "../component/admin-register-button";
import AdminsFilterCard from "../component/admin-filters-card";
import { AiOutlineLoading } from "react-icons/ai";

export default function AdminsLayout({
  isLoading = false,
  cards,
}: {
  cards: AdminSummaryCardProps[];
  isLoading?: boolean;
}) {
  if (isLoading) {
    return (
      <AppPageLayout className="flex items-center justify-center min-h-svh">
        <AiOutlineLoading className="text-7xl animate-spin" />
      </AppPageLayout>
    );
  }

  return (
    <AppPageLayout className="flex flex-col max-w-4xl">
      <DashboardTitle
        title="Gestão de Administradores"
        label="SISTEMA DE GESTÃO"
        description="Controle centralizado de permissões e acessos para a equipe de consultoria sanitária. Mantenha a integridade dos processos e a hierarquia de auditoria."
      />
      <AdminRegisterButton />
      <AdminsFilterCard />
      <div className="w-full mt-4">
        {cards.map((card, i) => (
          <AdminSummaryCard key={i} {...card} />
        ))}
      </div>
    </AppPageLayout>
  );
}
