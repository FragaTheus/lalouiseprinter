import { PerfilItem } from "@/shared/components/app/app-info-card";
import AppInfoLayout from "@/shared/components/layouts/app-info-layout";
import ReprintLabelWrapper from "../wrapper/reprint-label-wrapper";

export default function LabelInfoLayout({
  title,
  isLoading,
  isError,
  items,
  role,
}: {
  title: string;
  isLoading: boolean;
  isError: boolean;
  items: PerfilItem[];
  role: string | undefined;
}) {
  return (
    <AppInfoLayout
      description="painel da etiqueta"
      items={items}
      title={title}
      isError={isError}
      isLoading={isLoading}
    >
      {(role === "MANAGER" || role === "STAFF") && (
        <>
          <h2 className="font-semibold text-xl">Ações</h2>
          <ReprintLabelWrapper />
        </>
      )}
    </AppInfoLayout>
  );
}
