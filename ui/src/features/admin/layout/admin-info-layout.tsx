import { PerfilItem } from "@/shared/components/app/app-info-card";
import AppInfoLayout from "@/shared/components/layouts/app-info-layout";
import AdminChangePasswordCard from "../component/admin-change-password-card";
import AdminToggleActivationWrapper from "../wrapper/admin-toggle-activation-wrapper";
import AdminChangeNameWrapper from "../wrapper/admin-change-name-wrapper";

export default function AdminInfoLayout({
  title,
  isLoading,
  isError,
  items,
  isActive,
}: {
  title: string;
  isLoading: boolean;
  isError: boolean;
  items: PerfilItem[];
  isActive: boolean;
}) {
  return (
    <AppInfoLayout
      description="painel do membro"
      items={items}
      title={title}
      isError={isError}
      isLoading={isLoading}
    >
      <h2 className="font-semibold text-xl">
        Configuracoes da conta administrativa
      </h2>
      <AdminChangeNameWrapper />
      <AdminChangePasswordCard />
      <AdminToggleActivationWrapper isActive={isActive} />
    </AppInfoLayout>
  );
}
