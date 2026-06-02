import SectionLayout from "@/shared/components/layouts/section-layout";
import Image from "next/image";
import img from "@/shared/assets/home-about.jpg";

export default function About() {
  return (
    <SectionLayout sectionClassName="bg-card">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="flex flex-col items-start justify-center">
          <label className="text-xs font-bold opacity-90 uppercase text-muted-foreground">
            Sobre LaLouise
          </label>
          <h3 className="text-3xl font-bold mt-2">
            Experiência a serviço da sua cozinha
          </h3>
          <p className="mt-8 opacity-90 text-muted-foreground max-w-md">
            LaLouise é uma consultoria em segurança alimentar especializada em
            restaurantes orientais. Nossa equipe reúne profissionais com mais de
            15 anos de experiência. Entendemos culinária oriental no detalhe e
            desenvolvemos soluções específicas para cada operação.
          </p>
          <div className="mt-8 space-y-4">
            <div>
              <p className="font-bold text-sm">Equipe multidisciplinar</p>
              <p className="text-xs opacity-70">
                Biomédica, Consultora e Nutricionista
              </p>
            </div>
            <div>
              <p className="font-bold text-sm">60+ Restaurantes Atendidos</p>
              <p className="text-xs opacity-70">
                Todos em conformidade com a vigilância regional
              </p>
            </div>
            <div>
              <p className="font-bold text-sm">Atuação em Toda São Paulo</p>
              <p className="text-xs opacity-70">
                Cobrindo todas as regiões da capital
              </p>
            </div>
          </div>
        </div>
        <div className="relative">
          <Image src={img} alt="LaLouise" />
          <div className="bg-black absolute -bottom-4 -left-4 p-8 text-white shadow-sm">
            "Precisão é a nossa principal ferramenta de trabalho."
          </div>
        </div>
      </div>
    </SectionLayout>
  );
}
