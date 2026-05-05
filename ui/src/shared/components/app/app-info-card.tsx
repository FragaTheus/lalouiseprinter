import { Card, CardContent } from "@/shared/components/ui/card";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { ReactNode } from "react";

export interface PerfilItem {
  label: string;
  children: ReactNode;
}

export default function AppInfoCard({ items }: { items: PerfilItem[] }) {
  return (
    <Card className="w-full">
      <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {items.map((item, index) => (
          <Field key={index}>
            <FieldLabel className="uppercase">{item.label}</FieldLabel>
            <FieldContent>{item.children}</FieldContent>
          </Field>
        ))}
      </CardContent>
    </Card>
  );
}
