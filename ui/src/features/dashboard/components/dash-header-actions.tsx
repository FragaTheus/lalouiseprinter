"use client";

import { useLogout } from "@/features/login/hooks/useLogin";
import { Button } from "@/shared/components/ui/button";
import { LogOut, User } from "lucide-react";
import Link from "next/link";
import { usePathname } from "next/navigation";

export default function AppDashActios() {
  const pathname = usePathname();
  const logout = useLogout();
  const PROFILE = "/dashboard/profile";
  return (
    <div>
      {pathname === PROFILE ? (
        <Button
          variant={"ghost"}
          className="text-destructive"
          onClick={() => logout.mutate()}
        >
          <LogOut className="text-xl" />
        </Button>
      ) : (
        <Link href={PROFILE}>
          <Button variant={"ghost"}>
            <User className="text-xl" />
          </Button>
        </Link>
      )}
    </div>
  );
}
