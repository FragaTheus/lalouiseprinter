import { PerfilItem } from "../../../shared/components/app/app-info-card";
import ChangeNameCard from "../components/change-name-card";
import ChangePasswordCard from "../components/change-password-card";
import { ShieldCheck } from "lucide-react";
import AppInfoLayout from "@/shared/components/layouts/app-info-layout";

export default function ProfileLayout({
  title,
  items,
  isLoading,
  isError,
  role,
}: {
  title: string;
  items: PerfilItem[];
  isError: boolean;
  isLoading: boolean;
  role: string;
}) {
  return (
    <AppInfoLayout
      title={title}
      description="Perfil do membro"
      items={items}
      isLoading={isLoading}
      isError={isError}
    >
      {role != "STAFF" && (
        <>
          <h2 className="text-xl font-bold">Configurações da conta</h2>
          <div className="grid grid-cols-1 gap-4 mt-8">
            <ChangeNameCard />
            <ChangePasswordCard />
          </div>
        </>
      )}
      <span className="flex items-center gap-1 mt-8 text-secondary opacity-70 text-center flex-wrap justify-center">
        <ShieldCheck />
        <small>Seus dados criptografados de ponta a ponta</small>
      </span>
    </AppInfoLayout>
  );
}
