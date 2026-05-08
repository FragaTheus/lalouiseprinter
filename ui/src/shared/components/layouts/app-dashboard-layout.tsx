import DashCardLayout, {
  DashCardLayoutProps,
} from "@/features/dashboard/layout/dash-card-layout";

export default function AppDashboardLayout({
  cards,
}: {
  cards: DashCardLayoutProps[];
}) {
  return (
    <div className="w-full h-full grid grid-cols-1 md:grid-cols-3 gap-4">
      {cards.map((card, index) => (
        <DashCardLayout key={index} {...card} />
      ))}
    </div>
  );
}
