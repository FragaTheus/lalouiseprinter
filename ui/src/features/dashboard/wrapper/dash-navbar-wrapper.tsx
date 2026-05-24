"use client";

import AppNavBar, {
  ItemNavBarProps,
} from "@/shared/components/app/app-nav-bar";
import { Skeleton } from "@/shared/components/ui/skeleton";
import { Printer } from "lucide-react";
import { useParams, usePathname } from "next/navigation";
import { useEffect, useState } from "react";
import { BiHome, BiLabel, BiRestaurant } from "react-icons/bi";
import { CgInfo, CgProductHunt } from "react-icons/cg";
import { LiaBell } from "react-icons/lia";
import { MdDashboard } from "react-icons/md";
import { RiAdminFill } from "react-icons/ri";

export default function DashNavBarWrapper() {
  const { id: restaurantId, sectorId } = useParams<{
    id: string;
    sectorId: string;
  }>();

  const pathname = usePathname();
  const [isNavigating, setIsNavigating] = useState(false);
  const [resolvedPathname, setResolvedPathname] = useState(pathname);

  useEffect(() => {
    setIsNavigating(true);

    const timeout = setTimeout(() => {
      setResolvedPathname(pathname);
      setIsNavigating(false);
    }, 150);

    return () => clearTimeout(timeout);
  }, [pathname]);

  const base = "/dashboard";

  const dashLinks = [
    {
      label: "Painel",
      href: `${base}`,
      Icon: BiHome,
    },
    {
      label: "Restaurantes",
      href: `${base}/restaurants`,
      Icon: BiRestaurant,
    },
    {
      label: "Administradores",
      href: `${base}/admins`,
      Icon: RiAdminFill,
    },
  ] satisfies ItemNavBarProps[];

  const restaurantBase = `${base}/restaurants/${restaurantId}/resources`;

  const restaurantLinks = [
    {
      label: "Imprimir",
      href: `${restaurantBase}/labels/print`,
      Icon: Printer,
    },
    {
      label: "Produtos",
      href: `${restaurantBase}/products`,
      Icon: CgProductHunt,
    },
    {
      label: "Setores",
      href: `${restaurantBase}/sectors`,
      Icon: MdDashboard,
    },
    {
      label: "Colaboradores",
      href: `${restaurantBase}/staffs`,
      Icon: LiaBell,
    },
    {
      label: "Etiquetas",
      href: `${restaurantBase}/labels`,
      Icon: BiLabel,
    },
  ] satisfies ItemNavBarProps[];

  const sectorBase = `${restaurantBase}/sectors/${sectorId}/resources`;

  const sectorLinks = [
    {
      label: "Imprimir etiqueta",
      href: `${sectorBase}/labels/print`,
      Icon: Printer,
    },
    {
      label: "Etiquetas do setor",
      href: `${sectorBase}/labels/sector`,
      Icon: BiLabel,
    },
    {
      label: "Informações do setor",
      href: `${sectorBase}/info`,
      Icon: CgInfo,
    },
  ] satisfies ItemNavBarProps[];

  const links = pathname.includes("/sectors/")
    ? sectorLinks
    : pathname.includes(`/restaurants/${restaurantId}/resources`)
      ? restaurantLinks
      : dashLinks;

  if (isNavigating) {
    return (
      <nav className="w-full h-full flex items-center justify-evenly">
        {Array.from({ length: 3 }).map((_, i) => (
          <Skeleton key={i} className="h-8 w-16 rounded-md" />
        ))}
      </nav>
    );
  }

  return <AppNavBar links={links} />;
}
