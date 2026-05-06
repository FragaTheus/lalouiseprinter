"use client";

import { Card, CardContent } from "@/shared/components/ui/card";
import { Input } from "@/shared/components/ui/input";
import { SearchIcon } from "lucide-react";
import { ReactNode, useState } from "react";

export interface AppFilterCardProps {
  placeholder?: string;
  term: string;
  onTermChange: (term: string) => void;
  children?: ReactNode;
}

export default function AppFilterCard({
  term,
  onTermChange,
  placeholder = "Pesquisar...",
  children,
}: AppFilterCardProps) {
  const [value, setValue] = useState(term ?? "");

  return (
    <Card className="w-full shadow-none mt-4">
      <CardContent className="flex items-center justify-between gap-4 flex-wrap relative">
        <div className="relative flex items-center">
          <SearchIcon className="absolute left-3 size-4 text-muted-foreground" />
          <Input
            className="max-w-sm pl-10"
            placeholder={placeholder}
            value={value}
            onChange={(e) => {
              setValue(e.target.value);
              onTermChange(e.target.value);
            }}
          />
        </div>
        {children}
      </CardContent>
    </Card>
  );
}
