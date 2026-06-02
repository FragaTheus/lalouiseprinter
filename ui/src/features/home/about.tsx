import SectionLayout from "@/shared/components/layouts/section-layout";
import Image from "next/image";
import img from "@/shared/assets/home-about.jpg";

export default function About() {
  return (
    <SectionLayout>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="flex flex-col items-start justify-center">
          <label className="text-xs font-bold opacity-90 uppercase text-muted-foreground">
            Sobre LaLouise
          </label>
          <h3 className="text-3xl font-bold mt-2">
            Consultoria especializada em sua cozinha
          </h3>
          <p className="mt-8 opacity-90 text-muted-foreground max-w-md">
            Somos Biomédica, Consultora e Nutricionista. Essa combinação nos
            permite entregar segurança alimentar de verdade, sem complicação.
            Entendemos culinária oriental e sabemos exatamente o que você
            precisa.
          </p>
          <div className="mt-8 space-y-4">
            <div>
              <p className="font-bold text-sm">✓ Padrões microbiológicos</p>
              <p className="text-xs opacity-70">
                Higiene garantida em cada processo
              </p>
            </div>
            <div>
              <p className="font-bold text-sm">✓ Conformidade regulatória</p>
              <p className="text-xs opacity-70">
                Você em dia com a fiscalização
              </p>
            </div>
            <div>
              <p className="font-bold text-sm">✓ Segurança nutricional</p>
              <p className="text-xs opacity-70">Qualidade do insumo ao prato</p>
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
