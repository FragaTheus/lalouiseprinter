import Link from "next/link";
import { ElementType } from "react";
import { Button } from "../ui/button";

export interface ItemNavBarProps {
  href: string;
  label: string;
  Icon: ElementType;
  role?: string;
  userRole?: string;
}

const Items = ({ href, label, Icon, role, userRole }: ItemNavBarProps) => {
  if (role != userRole) {
    return null;
  }

  return (
    <Link href={href} role={role}>
      <Button variant={"link"} className="flex flex-col gap-2 cursor-pointer">
        <Icon className="size-5 lg:hidden" />
        <span className="text-xs hidden lg:block">{label}</span>
      </Button>
    </Link>
  );
};

interface AppNavBarProps {
  links: ItemNavBarProps[];
}

export default function AppNavBar({ links }: AppNavBarProps) {
  return (
    <nav className="w-full h-full flex items-center justify-evenly">
      {links.map((link, index) => (
        <Items
          key={index}
          href={link.href}
          label={link.label}
          Icon={link.Icon}
        />
      ))}
    </nav>
  );
}
