"use client";

import { useUserStore } from "@/store/user-store";
import PrintLabelStaffWrapper from "./print-label-staff-wrapper";
import PrintLabelManagerWrapper from "./print-label-manager-wrapper";

export default function PrintLabelWrapper() {
  const { user } = useUserStore();

  if (user?.role === "STAFF") return <PrintLabelStaffWrapper />;
  if (user?.role === "MANAGER") return <PrintLabelManagerWrapper />;

  return null;
}
