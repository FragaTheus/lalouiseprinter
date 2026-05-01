import { Providers } from "./providers";

type DashboardLayoutProps = {
  children: React.ReactNode;
};

export default function DashboardLayout({ children }: DashboardLayoutProps) {
  return <Providers>{children}</Providers>;
}
