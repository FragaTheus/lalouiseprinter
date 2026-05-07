import { PerfilItem } from "@/shared/components/app/app-info-card";
import AppInfoLayout from "@/shared/components/layouts/app-info-layout";
import ManagerChangeNameWrapper from "../wrapper/manager-change-name-wrapper";
import ManagerChangePasswordWrapper from "../wrapper/manager-change-password-wrapper";
import ManagerToggleActivationWrapper from "../wrapper/manager-toggle-activation-wrapper";

export default function ManagerInfoLayout({
  title,
  isProfile,
  isLoading,
  isError,
  items,
  isActive,
}: {
  title: string;
  isProfile: boolean;
  isLoading: boolean;
  isError: boolean;
  items: PerfilItem[];
  isActive: boolean;
}) {
  return (
    <AppInfoLayout
      description="painel do gerente"
      items={items}
      title={title}
      isError={isError}
      isLoading={isLoading}
    >
      <h2 className="font-semibold text-xl">Configurações da conta</h2>
      <ManagerChangeNameWrapper />
      <ManagerChangePasswordWrapper isProfile={isProfile} />
      {!isProfile && <ManagerToggleActivationWrapper isActive={isActive} />}
    </AppInfoLayout>
  );
}
