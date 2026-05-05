import { Button } from "@/shared/components/ui/button";
import heroImg from "@/shared/assets/home-hero.webp";
import Image from "next/image";
import Link from "next/link";

export default function HomeHero() {
  return (
    <div className="h-svh w-full grid grid-cols-1 lg:grid-cols-2 items-center justify-items-center gap-4 max-w-7xl m-auto p-4">
      <div className="flex flex-col order-last lg:order-first max-w-md">
        <h1 className="text-5xl md:text-7xl font-black tracking-tighter mb-6">
          Sua cozinha oriental no padrão que ela merece
        </h1>
        <p className="text-secondary mb-10">
          Especialistas em segurança alimentar para culinária japonesa e
          asiática. Precisão técnica com o respeito que a sua arte exige.
        </p>
        <div className="flex flex-wrap items-center gap-2">
          <Button className="p-6">Fale com a nossa equipe</Button>
          <Link href="/auth/login">
            <Button variant={"outline"} className="p-6">
              Entrar
            </Button>
          </Link>
        </div>
      </div>
      <Image
        src={heroImg}
        alt="Home Hero"
        className="hidden lg:block max-w-lg"
      />
    </div>
  );
}
