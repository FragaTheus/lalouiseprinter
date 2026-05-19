"use client";

import AppDashboardLayout from "@/shared/components/layouts/app-dashboard-layout";
import { DashCardLayoutProps } from "../layout/dash-card-layout";
import { RiUserShared2Fill } from "react-icons/ri";
import { HiUsers } from "react-icons/hi";
import {
  MdDashboard,
  MdInventory,
  MdLabel,
  MdInfo,
  MdTimeline,
} from "react-icons/md";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import DashboardTitle from "../components/dash-title";
import { useParams } from "next/navigation";
import { BiPlus, BiPrinter } from "react-icons/bi";
import { useRestaurantLookUpInfo } from "@/features/restaurant/hooks/use-restaurant";
import AppRouteGuard from "@/shared/components/app/app-route-guard";
import { useUserStore } from "@/store/user-store";
import { FaMapMarkedAlt } from "react-icons/fa";
import { LuScanSearch } from "react-icons/lu";

export default function DashRestaurantWrapper() {
  const { id } = useParams<{ id: string }>();
  const base = `/dashboard/restaurants/${id}`;
  const { data } = useRestaurantLookUpInfo(id);
  const { user } = useUserStore();

  const cards = [
    {
      href: `${base}/resources/labels/print`,
      Icon: BiPrinter,
      title: "Imprimir nova etiqueta",
      description: "Imprima uma nova etiqueta para qualquer um dos setores.",
    },
    {
      href: `${base}/resources/labels`,
      Icon: MdLabel,
      title: "Etiquetas",
      description:
        "Monitoramento global de etiquetas emitidas, validades e conformidade sanitária.",
    },
    {
      href: `${base}/resources/labels/lot`,
      Icon: MdTimeline,
      title: "Rastreio",
      description:
        "Rastreie todas as etiquetas associadas a um lote específico do restaurante.",
    },
    {
      href: `${base}/resources/products/register`,
      Icon: BiPlus,
      title: "Registrar novo produto",
      description:
        "Catálogo técnico de ingredientes, fichas de segurança química e especificações de fornecedores.",
    },
    {
      className: "lg:col-span-2",
      href: `${base}/resources/products`,
      Icon: MdInventory,
      title: "Produtos",
      description:
        "Catálogo técnico de ingredientes, fichas de segurança química e especificações de fornecedores.",
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
      href: `${base}/resources/sectors/register`,
      Icon: BiPlus,
      title: "Registrar setor",
      description: "Cadastrar novo setor para a sua unidade.",
    },
    {
      href: `${base}/resources/managers/register`,
      Icon: BiPlus,
      title: "Novo Gerente",
      description:
        "Configure as credenciais de acesso do gerente para este restaurante.",
    },
    {
      className: "lg:col-span-2",
      href: `${base}/resources/managers`,
      Icon: RiUserShared2Fill,
      title: "Gerentes",
      description:
        "Coordenação de responsáveis técnicos e gestores operacionais por unidade.",
    },
    {
      className: "lg:col-span-2",
      href: `${base}/resources/staffs`,
      Icon: HiUsers,
      title: "Colaboradores",
      description:
        "Registro de staff, certificados de treinamento e históricos de conduta sanitária.",
    },
    {
      href: `${base}/resources/staffs/register`,
      Icon: BiPlus,
      title: "Novo colaborador",
      description:
        "Coordenação de colaboradores operacionais para unidade do seu restaurante.",
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
          description="Bem-vindo à Lalouise. Monitore padrões, gerencie acessos e assegure a excelência higiênica em todos os pontos de operação com nossa interface de alta precisão."
        />
        <AppDashboardLayout cards={cards} />
      </AppPageLayout>
    </AppRouteGuard>
  );
}
