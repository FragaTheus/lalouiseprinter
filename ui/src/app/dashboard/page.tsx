import DashboardTitle from "@/features/dashboard/components/dash-title";
import DashboardWrapper from "@/features/dashboard/wrapper/dash-wrapper";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";

export default function DashboardLayout() {
  return (
    <AppPageLayout>
      <DashboardTitle
        label="SISTEMA DE GESTÃO SANITÁRIAs"
        title="Central de Gestão"
        description="Bem-vindo à Lalouise. Monitore padrões, gerencie acessos e assegure a excelência higiênica em todos os pontos de operação com nossa interface de alta precisão."
      />
      <DashboardWrapper />
    </AppPageLayout>
  );
}
