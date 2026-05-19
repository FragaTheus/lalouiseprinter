import PrintLabelManagerWrapper from "@/features/label/wrapper/print-label-manager-wrapper";
import AppInputPageLayout from "@/shared/components/layouts/app-input-page-layout";
import {
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";

export default function PrintLabel() {
  return (
    <AppInputPageLayout>
      <CardHeader>
        <CardTitle>Imprimir</CardTitle>
        <CardDescription>
          Imprima uma etiqueta para o setor escolhido
        </CardDescription>
      </CardHeader>
      <CardContent>
        <PrintLabelManagerWrapper />
      </CardContent>
    </AppInputPageLayout>
  );
}
