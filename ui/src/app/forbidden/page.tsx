"use client";

import { Button } from "@/shared/components/ui/button";
import { useRouter } from "next/navigation";

export default function Forbidden() {
  const { back } = useRouter();
  return (
    <main className="h-svh w-full flex items-center justify-center flex-col p-4">
      <h1 className="font-black tracking-tighter text-7xl">403</h1>
      <h2 className="text-xl font-semibold">Não autorizado</h2>
      <p className="mt-4 text-center text-muted-foreground">
        Você não tem permissão para acessar este recurso.
      </p>

      <Button variant={"link"} className="mt-8" onClick={back}>
        Voltar
      </Button>
    </main>
  );
}
