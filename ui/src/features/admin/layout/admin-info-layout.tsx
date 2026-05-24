import { PerfilItem } from "@/shared/components/app/app-info-card";
import AppInfoLayout from "@/shared/components/layouts/app-info-layout";
import AdminToggleActivationWrapper from "../wrapper/admin-toggle-activation-wrapper";
import AdminChangeNameWrapper from "../wrapper/admin-change-name-wrapper";
import AdminChangePasswordWrapper from "../wrapper/admin-change-password-wrapper";

export default function AdminInfoLayout({
  title,
  isProfile,
  isLoading,
  isError,
  items,
  isActive,
  roles,
  userRole,
}: {
  title: string;
  isProfile: boolean;
  isLoading: boolean;
  isError: boolean;
  items: PerfilItem[];
  isActive: boolean;
  roles: string[];
  userRole: string;
}) {
  return (
    <AppInfoLayout
      description="painel do membro"
      items={items}
      title={title}
      isError={isError}
      isLoading={isLoading}
      roles={roles}
      userRole={userRole}
    >
      <h2 className="font-semibold text-xl">
        Configuracoes da conta administrativa
      </h2>
      <AdminChangeNameWrapper />
      <AdminChangePasswordWrapper isProfile={isProfile} />
      {!isProfile && <AdminToggleActivationWrapper isActive={isActive} />}
    </AppInfoLayout>
  );
}
