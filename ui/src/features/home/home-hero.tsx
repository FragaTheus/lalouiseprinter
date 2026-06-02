import heroImg from "@/shared/assets/home-hero.webp";
import Image from "next/image";
import SectionLayout from "@/shared/components/layouts/section-layout";
import { Badge } from "@/shared/components/ui/badge";
import CtaBtn from "@/shared/components/app/cta-btn";

export default function HomeHero() {
  return (
    <SectionLayout>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="flex flex-col order-last md:order-first max-w-sm md:max-w-md justify-center">
          <h1 className="text-4xl lg:text-6xl xl:text-7xl font-black tracking-tighter mb-6">
            Sua cozinha oriental no padrão que ela merece
          </h1>
          <p className="text-secondary mb-10 text-sm">
            Especialistas em segurança alimentar para restaurantes orientais.
            Rastreabilidade, documentação, controle de qualidade. Tudo que você
            precisa em um lugar.
          </p>
          <CtaBtn
            children="Comece sua consultoria"
            className="w-full p-6"
            message="Oi Thaís! Vim pelo site e quero saber mais sobre a consultoria."
          />
          <div className="flex flex-col md:flex-row items-start gap-2 mt-8 ">
            {items.map((item, index) => (
              <Badge
                key={index}
                className="flex flex-col"
                variant={"secondary"}
              >
                {item.description}
              </Badge>
            ))}
          </div>
        </div>
        <Image
          src={heroImg}
          alt="Home Hero"
          className="w-full max-w-2/3 md:max-w-full"
        />
      </div>
    </SectionLayout>
  );
}

const items = [
  {
    description: "15+ Anos de experiência",
  },
  {
    description: "13+ Cidades atuantes",
  },
  {
    description: "100% Conformidade",
  },
];
