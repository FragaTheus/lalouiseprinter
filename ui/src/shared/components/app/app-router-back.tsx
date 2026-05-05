"use client";

import { useRouter } from "next/navigation";
import { Button } from "../ui/button";
import { ChevronsLeft } from "lucide-react";
import { cn } from "@/lib/utils";

export default function AppRouterBack({ className }: { className?: string }) {
  const { back } = useRouter();
  return (
    <Button
      variant={"ghost"}
      onClick={back}
      className={cn("lg:hidden", className)}
      title="Voltar"
    >
      <ChevronsLeft className="text-4xl" />
    </Button>
  );
}
