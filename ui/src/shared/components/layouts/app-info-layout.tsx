import DashboardTitle from "@/features/dashboard/components/dash-title";
import { AiOutlineLoading } from "react-icons/ai";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import ProfileCard, { PerfilItem } from "@/shared/components/app/app-info-card";

export default function AppInfoLayout({
  title,
  description,
  items,
  isLoading,
  isError,
  children,
}: {
  title: string;
  description: string;
  items: PerfilItem[];
  isError: boolean;
  isLoading: boolean;
  children: React.ReactNode;
}) {
  return (
    <AppPageLayout className="min-h-svh max-w-xl! flex flex-col items-center justify-center">
      {isLoading ? (
        <AiOutlineLoading className="text-7xl animate-spin" />
      ) : isError ? (
        <h2 className="text-destructive text-3xl">
          Erro ao carregar dados do perfil.
        </h2>
      ) : (
        <div className="w-full h-full">
          <DashboardTitle label={description} title={title} />
          <div className="w-full h-full flex flex-col">
            <ProfileCard items={items} />
            <div className="w-full h-full mt-8 flex flex-col gap-4">
              {children}
            </div>
          </div>
        </div>
      )}
    </AppPageLayout>
  );
}
