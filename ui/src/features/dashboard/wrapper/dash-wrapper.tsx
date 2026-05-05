"use client";

import { RiAdminFill, RiUserShared2Fill } from "react-icons/ri";
import DashCardLayout, {
  DashCardLayoutProps,
} from "../layout/dash-card-layout";
import {
  MdDashboard,
  MdInventory,
  MdLabel,
  MdRestaurant,
} from "react-icons/md";
import { HiUsers } from "react-icons/hi";

const cards = [
  {
    roles: ["ADMIN"],
    className: "md:col-span-2",
    href: "/dashboard/admins",
    Icon: RiAdminFill,
    title: "Administradores",
    description:
      "Gerencie perfis de alta hierarquia, permissões globais do sistema e logs de segurança crítica.",
  },
  {
    roles: ["ADMIN"],
    href: "/dashboard/restaurants",
    Icon: MdRestaurant,
    title: "Restaurantes",
    description:
      "Controle de unidades, licenças sanitárias e perfis de estabelecimentos parceiros.",
  },
  {
    href: "/dashboard/managers",
    Icon: RiUserShared2Fill,
    title: "Gerentes",
    description:
      "Coordenação de responsáveis técnicos e gestores operacionais por unidade.",
  },
  {
    href: "/dashboard/staffs",
    Icon: HiUsers,
    title: "Colaboradores",
    description:
      "Registro de staff, certificados de treinamento e históricos de conduta sanitária.",
  },
  {
    href: "/dashboard/sectors",
    Icon: MdDashboard,
    title: "Setores",
    description:
      "Sistema de rotulagem, datas de validade e controle de rastreabilidade de insumos.",
  },
  {
    href: "/dashboard/labels",
    Icon: MdLabel,
    title: "Etiquetas",
    description:
      "Sistema de rotulagem, datas de validade e controle de rastreabilidade de insumos.",
  },
  {
    className: "lg:col-span-2",
    href: "/dashboard/products",
    Icon: MdInventory,
    title: "Produtos",
    description:
      "Catálogo técnico de ingredientes, fichas de segurança química e especificações de fornecedores.",
  },
] satisfies DashCardLayoutProps[];

export default function DashboardWrapper() {
  return (
    <div className="w-full h-full grid grid-cols-1 md:grid-cols-3 gap-4">
      {cards.map((card, index) => (
        <DashCardLayout key={index} {...card} />
      ))}
    </div>
  );
}
