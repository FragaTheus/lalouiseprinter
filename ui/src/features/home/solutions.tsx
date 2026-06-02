import SectionLayout from "@/shared/components/layouts/section-layout";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import { ElementType } from "react";
import { BiShield } from "react-icons/bi";
import { FaFile, FaUserGroup } from "react-icons/fa6";
import { MdKitchen } from "react-icons/md";

interface SolutionCardProps {
  icon: ElementType;
  title: string;
  description: string;
}

const SolutionCard = ({
  icon: Icon,
  title,
  description,
}: SolutionCardProps) => {
  return (
    <Card className="outline-none! ring-0! bg-popover border border-transparent hover:border-border transition-colors">
      <CardHeader>
        <Icon className="size-8" />
        <CardTitle className="mt-2 text-xl whitespace-pre-line">
          {title}
        </CardTitle>
      </CardHeader>
      <CardContent>
        <CardDescription className="text-base">{description}</CardDescription>
      </CardContent>
    </Card>
  );
};

export default function Solutions() {
  return (
    <SectionLayout sectionClassName="bg-card">
      <label className="text-xs font-bold opacity-90 uppercase text-muted-foreground">
        Nossas soluções
      </label>
      <h3 className="text-3xl font-bold mt-2">Consultoria especializada</h3>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mt-8">
        {solutions.map((solution) => (
          <SolutionCard key={solution.title} {...solution} />
        ))}
      </div>
    </SectionLayout>
  );
}

const solutions = [
  {
    icon: BiShield,
    title: "Segurança\n alimentar",
    description:
      "Baseando-se na vigilância sanitária da sua região, garantimos que sua cozinha fique em conformidade com as normas vigentes.",
  },
  {
    icon: FaUserGroup,
    title: "Treinamento\n de funcionários",
    description:
      "Treinamento teórico e prático para os colaboradores, ensinando como manipular alimentos de forma segura para melhor qualidade dos alimentos.",
  },
  {
    icon: MdKitchen,
    title: "Adequação\n da cozinha",
    description:
      "Design operacional personalizado para sua unidade, focado em segurança alimentar, eficiência e redução de desperdícios.",
  },
  {
    icon: FaFile,
    title: "Documentação\n e rotulagem",
    description:
      "Gestão completa de documentação (POPs, manuais, boas práticas) e rotulagem de alimentos. Tudo que você precisa para estar em conformidade.",
  },
] satisfies SolutionCardProps[];
