import { Label } from "@/shared/components/ui/label";

export default function DashboardTitle({
  label,
  title,
  description,
}: {
  label: string;
  title: string;
  description?: string;
}) {
  return (
    <div className="w-full mb-8">
      <Label className="mb-4 uppercase text-muted-foreground text-xs">
        {label}
      </Label>
      <h1 className="text-3xl md:text-5xl lg:text-7xl font-bold mb-2">
        {title}
      </h1>
      <div className="h-1 bg-primary w-1/10 mb-4" />
      {description && (
        <p className="text-muted-foreground max-w-md">{description}</p>
      )}
    </div>
  );
}
