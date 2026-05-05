"use client";

import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import { useUserStore } from "@/store/user-store";
import Link from "next/link";
import { ElementType } from "react";

export interface DashCardLayoutProps {
  href: string;
  title: string;
  description: string;
  Icon: ElementType;
  className?: string;
  roles?: string[];
}

export default function DashCardLayout({
  href,
  title,
  description,
  Icon,
  className,
  roles,
}: DashCardLayoutProps) {
  const { user } = useUserStore();

  if (roles && user && !roles.includes(user.role)) return null;

  return (
    <Link href={href} className={className}>
      <Card className="h-full w-full hover:bg-accent transition-colors">
        <CardHeader className="h-full flex flex-col justify-between">
          <Icon className="text-3xl md:text-4xl lg:text-7xl" />
          <CardTitle>{title}</CardTitle>
          <CardDescription>{description}</CardDescription>
        </CardHeader>
      </Card>
    </Link>
  );
}
