import SectionLayout from "@/shared/components/layouts/section-layout";

interface ItemProps {
  number: string;
  title: string;
  description: string;
}

const Item = ({ number, title, description }: ItemProps) => {
  return (
    <div className="flex flex-col items-start justify-center gap-4">
      <span className="bg-black text-white p-4 rounded-md uppercase text-xl font-black">
        {number}
      </span>
      <h3 className="text-xl font-bold">{title}</h3>
      <p className="text-muted-foreground opacity-90">{description}</p>
    </div>
  );
};

export default function HowTo() {
  return (
    <SectionLayout>
      <label className="text-xs font-bold opacity-90 uppercase text-muted-foreground">
        Simples de contratar
      </label>
      <h3 className="text-3xl font-bold mt-2">Etapas do Processo</h3>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-10 mt-8">
        {items.map((item) => (
          <Item key={item.number} {...item} />
        ))}
      </div>
    </SectionLayout>
  );
}

const items = [
  {
    number: "01",
    title: "Diagnóstico",
    description:
      "Avaliação das condições atuais e identificação de riscos sanitários específicos da operação.",
  },
  {
    number: "02",
    title: "Plano de Adequação",
    description:
      "Desenvolvimento de estratégias personalizadas e cronograma para correção de irregularidades.",
  },
  {
    number: "03",
    title: "Acompanhamento",
    description:
      "Monitoramento contínuo e suporte técnico para garantir a manutenção dos padrões de excelência.",
  },
] satisfies ItemProps[];
