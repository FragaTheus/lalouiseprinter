import { Card, CardContent } from "@/shared/components/ui/card";
import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import Link from "next/link";

export interface AdminSummaryFieldProps {
  label: string;
  children: React.ReactNode;
}

const AdminSummaryField = ({ label, children }: AdminSummaryFieldProps) => {
  return (
    <Field>
      <FieldLabel>{label}</FieldLabel>
      <FieldContent>{children}</FieldContent>
    </Field>
  );
};

export interface AdminSummaryCardProps {
  href?: string;
  fields: AdminSummaryFieldProps[];
}

export default function AdminSummaryCard({
  href = "#",
  fields,
}: AdminSummaryCardProps) {
  return (
    <Link href={href}>
      <Card className="bg-popover mt-2">
        <CardContent className="relative grid grid-cols-1 md:grid-cols-3 gap-4 items-center justify-items-center">
          {fields.map((f, i) => (
            <AdminSummaryField key={i} label={f.label}>
              {f.children}
            </AdminSummaryField>
          ))}
        </CardContent>
      </Card>
    </Link>
  );
}
