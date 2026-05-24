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
  roles,
  userRole,
}: {
  title: string;
  isLoading: boolean;
  isError: boolean;
  items: PerfilItem[];
  isActive: boolean;
  roles: string[];
  userRole: string;
}) {
  return (
    <AppInfoLayout
      description="painel do setor"
      items={items}
      title={title}
      isError={isError}
      isLoading={isLoading}
      roles={roles}
      userRole={userRole}
    >
      {(userRole === "MANAGER" || userRole === "STAFF") && (
        <>
          <h2 className="font-semibold text-xl">Configurações do setor</h2>
          <SectorChangeNameWrapper />
          <SectorChangeStoragesWrapper />
        </>
      )}
      {userRole === "MANAGER" && (
        <SectorToggleActivationWrapper isActive={isActive} />
      )}
    </AppInfoLayout>
  );
}
