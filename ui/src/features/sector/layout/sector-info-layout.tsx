import { PerfilItem } from "@/shared/components/app/app-info-card";
import AppInfoLayout from "@/shared/components/layouts/app-info-layout";
import SectorChangeNameWrapper from "../wrapper/sector-change-name-wrapper";
import SectorChangeStoragesWrapper from "../wrapper/sector-change-storages-wrapper";
import SectorToggleActivationWrapper from "../wrapper/sector-toggle-activation-wrapper";

export default function SectorInfoLayout({
  title,
  isLoading,
  isError,
  items,
  isActive,
  role,
}: {
  title: string;
  isLoading: boolean;
  isError: boolean;
  items: PerfilItem[];
  isActive: boolean;
  role?: "ADMIN" | "MANAGER" | "STAFF";
}) {
  return (
    <AppInfoLayout
      description="painel do setor"
      items={items}
      title={title}
      isError={isError}
      isLoading={isLoading}
    >
      {/* ADMIN não pode alterar dados ou desativar setores */}
      {(role === "MANAGER" || role === "STAFF") && (
        <>
          <h2 className="font-semibold text-xl">Configurações do setor</h2>
          <SectorChangeNameWrapper />
          <SectorChangeStoragesWrapper />
        </>
      )}
      {/* Somente MANAGER pode ativar/desativar setores */}
      {role === "MANAGER" && (
        <SectorToggleActivationWrapper isActive={isActive} />
      )}
    </AppInfoLayout>
  );
}
