"use client";

import { useParams } from "next/navigation";
import { useDeactiveAdmin } from "../hook/use-admin";
import { Button } from "@/shared/components/ui/button";

export default function DeactiveAdminBtn() {
  const { id } = useParams<{ id: string }>();
  const { mutate } = useDeactiveAdmin(id);
  return (
    <Button variant={"destructive"} onClick={() => mutate()}>
      Desativar administrador
    </Button>
  );
}
