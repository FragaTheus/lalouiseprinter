import { PerfilItem } from "@/shared/components/app/app-info-card";
import AppInfoLayout from "@/shared/components/layouts/app-info-layout";
import ProductToggleActivationWrapper from "../wrapper/product-toggle-activation-wrapper";
import ProductChangeNameWrapper from "../wrapper/product-change-name-wrapper";
import ProductChangeDescriptionWrapper from "../wrapper/product-change-category-wrapper";

export default function ProductInfoLayout({
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
  role: string | undefined;
  roles: string[];
  userRole: string;
}) {
  return (
    <AppInfoLayout
      description="painel do produto"
      items={items}
      title={title}
      isError={isError}
      isLoading={isLoading}
      roles={roles}
      userRole={userRole}
    >
      <h2 className="font-semibold text-xl">Configurações do produto</h2>
      <ProductChangeNameWrapper />
      <ProductChangeDescriptionWrapper />
      <ProductToggleActivationWrapper isActive={isActive} />
    </AppInfoLayout>
  );
}
