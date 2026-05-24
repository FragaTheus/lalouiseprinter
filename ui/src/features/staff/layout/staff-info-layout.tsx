import { PerfilItem } from "@/shared/components/app/app-info-card";
import AppInfoLayout from "@/shared/components/layouts/app-info-layout";
import StaffChangeNameWrapper from "../wrapper/staff-change-name-wrapper";
import StaffChangePasswordWrapper from "../wrapper/staff-change-password-wrapper";
import StaffToggleActivationWrapper from "../wrapper/staff-toggle-activation-wrapper";

export default function StaffInfoLayout({
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
      description="painel do colaborador"
      items={items}
      title={title}
      isError={isError}
      isLoading={isLoading}
      userRole={userRole}
      roles={roles}
    >
      <h2 className="font-semibold text-xl">Configurações da conta</h2>
      <StaffChangeNameWrapper />
      <StaffChangePasswordWrapper />
      <StaffToggleActivationWrapper isActive={isActive} />
    </AppInfoLayout>
  );
}
