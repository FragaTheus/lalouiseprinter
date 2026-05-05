import { Card, CardContent } from "@/shared/components/ui/card";
import { Input } from "@/shared/components/ui/input";
import { SearchIcon } from "lucide-react";

export default function AdminsFilterCard() {
  return (
    <Card className="w-full shadow-none mt-4">
      <CardContent className="flex items-center justify-between relative">
        <SearchIcon className="absolute left-8" />
        <Input
          className="max-w-sm pl-10"
          placeholder="Buscar por nome ou email..."
        />
      </CardContent>
    </Card>
  );
}
