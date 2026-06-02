import { Button } from "@/shared/components/ui/button";
import heroImg from "@/shared/assets/home-hero.webp";
import Image from "next/image";
import SectionLayout from "@/shared/components/layouts/section-layout";

interface ItemProps {
  title: string;
  description: string;
}

const Item = ({ title, description }: ItemProps) => {
  return (
    <div className="flex flex-col items-start text-start w-full m-auto">
      <h3 className="font-bold uppercase text-sm">{title}</h3>
      <p className="text-muted-foreground text-xs opacity-90">{description}</p>
    </div>
  );
};

export default function HomeHero() {
  return (
    <SectionLayout>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="flex flex-col order-last md:order-first max-w-sm md:max-w-md justify-center">
          <h1 className="text-4xl lg:text-6xl xl:text-7xl font-black tracking-tighter mb-6">
            Sua cozinha oriental no padrão que ela merece
          </h1>
          <p className="text-secondary mb-10 text-sm">
            Especialistas em segurança alimentar para culinária japonesa e
            asiática. Precisão técnica com o respeito que a sua arte exige.
          </p>
          <Button className="p-6">Fale com a nossa equipe</Button>
          <div className="flex flex-col md:flex-row items-start gap-2 mt-8 ">
            {items.map((item) => (
              <Item key={item.title} {...item} />
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
    title: "15",
    description: "Anos de experiência",
  },
  {
    title: "13",
    description: "Cidades atuantes",
  },
  {
    title: "100%",
    description: "Conformidade",
  },
] satisfies ItemProps[];
