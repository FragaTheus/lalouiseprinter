import DashboardTitle from "@/features/dashboard/components/dash-title";
import AppPageLayout from "@/shared/components/layouts/app-page-layout";
import Link from "next/link";
import { AiOutlineLoading } from "react-icons/ai";
import { Button } from "../ui/button";
import AppSummaryCard, { AppSummaryCardProps } from "../app/app-summary-card";
import AppFilterCard, { AppFilterCardProps } from "../app/app-filter-card";
import { RefObject } from "react";

type Roles = "ADMIN" | "MANAGER" | "STAFF";

export interface AppListLayoutProps {
  titleLabel: string;
  title: string;
  titleDescription: string;
  href: string;
  registerText: string;
  cards: AppSummaryCardProps[];
  isLoading?: boolean;
  isListLoading?: boolean;
  sentinelRef?: RefObject<HTMLDivElement | null>;
  filterProps: AppFilterCardProps;
  roles?: Roles[];
  currentRole?: string | undefined;
}

export default function AppListLayout(props: AppListLayoutProps) {
  const { isLoading, cards, sentinelRef, isListLoading, filterProps } = props;

  if (isLoading) {
    return (
      <AppPageLayout className="flex items-center justify-center min-h-svh">
        <AiOutlineLoading className="text-7xl animate-spin" />
      </AppPageLayout>
    );
  }

  return (
    <AppPageLayout className="flex flex-col max-w-4xl">
      <DashboardTitle
        title={props.title}
        label={props.titleLabel}
        description={props.titleDescription}
      />

      {(!props.roles || props.roles.includes(props.currentRole as Roles)) && (
        <Link href={props.href}>
          <Button>{props.registerText}</Button>
        </Link>
      )}

      <AppFilterCard {...filterProps} />
      <div className="w-full mt-4">
        {cards.map((card, i) => (
          <AppSummaryCard key={i} {...card} />
        ))}
      </div>
      {isListLoading && (
        <AiOutlineLoading className="text-4xl animate-spin mx-auto my-4" />
      )}
      {sentinelRef && <div ref={sentinelRef} className="h-1" />}
    </AppPageLayout>
  );
}
