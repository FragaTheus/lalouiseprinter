import { PerfilItem } from "@/shared/components/app/app-info-card";
import AppInfoLayout from "@/shared/components/layouts/app-info-layout";
import ReprintLabelManagerWrapper from "../wrapper/reprint-label-manager-wrapper";
import ReprintLabelStaffWrapper from "../wrapper/reprint-label-staff-wrapper";
import ReprintLabelWrapper from "../wrapper/reprint-label-wrapper";

export default function LabelInfoLayout({
  title,
  isLoading,
  isError,
  items,
  roles,
  userRole,
}: {
  title: string;
  isLoading: boolean;
  isError: boolean;
  items: PerfilItem[];
  role: string | undefined;
  roles: string[];
  userRole: string;
}) {
  return (
    <AppInfoLayout
      description="painel da etiqueta"
      items={items}
      title={title}
      isError={isError}
      isLoading={isLoading}
      roles={roles}
      userRole={userRole}
    >
      <h2 className="font-semibold text-xl">Ações</h2>
      {userRole === "STAFF" ? (
        <ReprintLabelStaffWrapper />
      ) : (
        <ReprintLabelManagerWrapper />
      )}
      <ReprintLabelWrapper />
    </AppInfoLayout>
  );
}
