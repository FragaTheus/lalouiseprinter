import AppHeader from "@/shared/components/app/app-header";
import AppLogo from "@/shared/components/app/app-logo";
import CtaBtn from "@/shared/components/app/cta-btn";
import { Button } from "@/shared/components/ui/button";
import { LogIn } from "lucide-react";
import Link from "next/link";
import { BsWhatsapp } from "react-icons/bs";

export default function HomeHeader() {
  return (
    <AppHeader>
      <AppLogo />
      <div className="flex items-center gap-2">
        <Link href="/auth/login">
          <Button variant={"outline"}>
            <LogIn />
            <span className="hidden md:inline">Entrar</span>
          </Button>
        </Link>
        <CtaBtn>
          <BsWhatsapp />
          <span className=" hidden md:inline">Fale conosco</span>
        </CtaBtn>
      </div>
    </AppHeader>
  );
}
