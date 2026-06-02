import SectionLayout from "@/shared/components/layouts/section-layout";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import { ElementType } from "react";
import { BiCalendar } from "react-icons/bi";
import { BsQrCode } from "react-icons/bs";
import { CgBell } from "react-icons/cg";

interface ItemProps {
  icon: ElementType;
  title: string;
  description: string;
}

const Item = ({ icon: Icon, title, description }: ItemProps) => {
  return (
    <Card className="rounded-none! max-w-xs bg-popover">
      <CardHeader>
        <Icon className="size-8" />
        <CardTitle className="mt-2 whitespace-pre-line">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <CardDescription>{description}</CardDescription>
      </CardContent>
    </Card>
  );
};

export default function Printer() {
  return (
    <SectionLayout>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="flex flex-col items-start justify-center">
          <label className="text-xs font-bold opacity-90 uppercase text-muted-foreground">
            Tecnologia Proprietária
          </label>
          <h3 className="text-3xl font-bold mt-2">Sistema de Etiquetas</h3>
          <p className="mt-8 opacity-90 text-muted-foreground max-w-md">
            Nosso sistema de etiquetagem começa na primeira manipulação e
            acompanha cada insumo até o prato. Para você: controle total de
            qualidade e validade. Para a vigilância: rastreabilidade completa e
            conformidade documentada.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {items.map((item, index) => (
            <Item key={index} {...item} />
          ))}
        </div>
      </div>
    </SectionLayout>
  );
}

const items = [
  {
    icon: BsQrCode,
    title: "Rastreabilidade\n Total",
    description:
      "Histórico completo de cada insumo, do fornecedor até o prato final.",
  },
  {
    icon: BiCalendar,
    title: "Controle de\n Qualidade",
    description: "Automação precisa de prazos e condições de manipulação.",
  },
  {
    icon: CgBell,
    title: "Avisos\n Inteligentes",
    description:
      "Notificações de vencimento e perda de produto antes do problema.",
  },
] satisfies ItemProps[];
