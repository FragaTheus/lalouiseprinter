import { Card, CardContent } from "@/shared/components/ui/card";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import Link from "next/link";

export interface AppSummaryCardItemProps {
  label: string;
  children: React.ReactNode;
}

const AppSummaryCardItem = ({ label, children }: AppSummaryCardItemProps) => {
  return (
    <Field>
      <FieldLabel>{label}</FieldLabel>
      <FieldContent>{children}</FieldContent>
    </Field>
  );
};

export interface AppSummaryCardProps {
  href?: string;
  fields: AppSummaryCardItemProps[];
}

export default function AppSummaryCard({
  href = "#",
  fields,
}: AppSummaryCardProps) {
  return (
    <Link href={href}>
      <Card className="bg-popover mt-2">
        <CardContent className="relative grid grid-cols-1 md:grid-cols-3 gap-4 items-center justify-items-center">
          {fields.map((f, i) => (
            <AppSummaryCardItem key={i} label={f.label}>
              {f.children}
            </AppSummaryCardItem>
          ))}
        </CardContent>
      </Card>
    </Link>
  );
}
