import SectionLayout from "@/shared/components/layouts/section-layout";
import { Button } from "@/shared/components/ui/button";

export default function Cta() {
  return (
    <SectionLayout sectionClassName="bg-black text-white">
      <div className="flex items-center justify-center flex-col text-center">
        <h1 className="font-bold text-4xl md:text-6xl tracking-tighter">
          Sua cozinha no padrão que ela merece
        </h1>
        <p className="opacity-90 mt-4">
          Agende uma visita hoje e eleve o patamar de segurança do seu
          restaurante.
        </p>
        <Button className="bg-white text-black px-12 font-label-caps uppercase tracking-widest hover:opacity-90 hover:bg-white mt-12">
          Solicitar Diagnóstico
        </Button>
      </div>
    </SectionLayout>
  );
}
