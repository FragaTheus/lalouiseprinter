import { PerfilItem } from "@/shared/components/app/app-info-card";
import AppInfoLayout from "@/shared/components/layouts/app-info-layout";
import AdminChangeNameCard from "../component/admin-change-name-card";
import AdminChangePasswordCard from "../component/admin-change-password-card";
import DeactiveAdminBtn from "../component/deactive-admin-btn";

export default function AdminInfoLayout({
  title,
  isLoading,
  isError,
  items,
}: {
  title: string;
  isLoading: boolean;
  isError: boolean;
  items: PerfilItem[];
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
      <AdminChangeNameCard />
      <AdminChangePasswordCard />
      <DeactiveAdminBtn />
    </AppInfoLayout>
  );
}
