import { Button } from "@/shared/components/ui/button";
import { PlusIcon } from "lucide-react";
import Link from "next/link";

export default function AdminRegisterButton() {
  return (
    <Link href={"/dashboard/admins/register"}>
      <Button className="max-w-3xs">
        <PlusIcon />
        <span>Cadastrar administrador</span>
      </Button>
    </Link>
  );
}
