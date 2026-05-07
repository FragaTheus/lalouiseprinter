import { Button } from "@/shared/components/ui/button";
import Link from "next/link";

export default function Forbidden() {
  return (
    <main className="h-svh w-full flex items-center justify-center flex-col p-4">
      <h1 className="font-black tracking-tighter text-7xl">403</h1>
      <h2 className="text-xl font-semibold">Não autorizado</h2>
      <p className="mt-4 text-center text-muted-foreground">
        Você não tem permissão para acessar esta página.
      </p>

      <Link href={"/dashboard"} className="mt-8">
        <Button variant={"link"}>Voltar para o painel</Button>
      </Link>
    </main>
  );
}
