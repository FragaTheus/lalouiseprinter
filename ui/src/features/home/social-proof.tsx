import SectionLayout from "@/shared/components/layouts/section-layout";
import { Separator } from "@/shared/components/ui/separator";

interface ItemProps {
  title: string;
  description: string;
}

const Item = ({ title, description }: ItemProps) => {
  return (
    <div className="flex flex-col items-center justify-center text-center w-full m-auto">
      <h3 className="font-bold text-4xl md:text-5xl">{title}</h3>
      <p className="text-muted-foreground text-sm opacity-90">{description}</p>
    </div>
  );
};

export default function SocialProof() {
  return (
    <SectionLayout
      sectionClassName="bg-card border-y-2"
      containerClassName="grid grid-cols-1 md:grid-cols-7 items-center justify-items-center gap-8"
    >
      {items.flatMap((item, index) => [
        <Separator key={`sep-${index}`} orientation="vertical" />,
        <Item key={`item-${item.title}`} {...item} />,
      ])}
      <Separator key="sep-end" orientation="vertical" />
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
