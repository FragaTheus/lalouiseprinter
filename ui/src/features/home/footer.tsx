import AppLogo from "@/shared/components/app/app-logo";
import Link from "next/link";
import { BsPhone } from "react-icons/bs";
import { FaLocationPin } from "react-icons/fa6";
import { MdEmail } from "react-icons/md";

const FooterItemLayout = ({ children }: { children: React.ReactNode }) => {
  return (
    <div className="flex flex-col items-start justify-center gap-2">
      {children}
    </div>
  );
};

const FooterLink = ({
  href,
  children,
}: {
  href: string;
  children: React.ReactNode;
}) => {
  return (
    <Link
      href={href}
      className="text-muted-foreground text-xs opacity-90 flex items-center gap-2"
    >
      {children}
    </Link>
  );
};

const About = () => {
  return (
    <FooterItemLayout>
      <AppLogo />
      <span className="text-muted-foreground text-xs opacity-90">
        Excelência em Segurança Alimentar.
      </span>
    </FooterItemLayout>
  );
};

const Nav = () => {
  return (
    <FooterItemLayout>
      <span className="text-xl font-bold tracking-tight">Navegação</span>
      <nav className="flex flex-col gap-2">
        <FooterLink href="/" children="Entrar" />
        <FooterLink href="/" children="Soluções" />
        <FooterLink href="/" children="Sistema" />
        <FooterLink href="/" children="Etapas" />
        <FooterLink href="/" children="Sobre" />
      </nav>
    </FooterItemLayout>
  );
};

const Contact = () => {
  return (
    <FooterItemLayout>
      <span className="text-xl font-bold tracking-tight">Contato</span>

      <FooterLink href="mailto:thais.lalouisenutri@gmail.com">
        <MdEmail />
        thais.lalouisenutri@gmail.com
      </FooterLink>
      <FooterLink href="tel:+55119996625687">
        <BsPhone />
        +55 (11) 99662-5687
      </FooterLink>
      <FooterLink href="https://www.google.com/maps?q=São+Paulo,+SP+-+Brasil">
        <FaLocationPin />
        São Paulo, SP - Brasil
      </FooterLink>
    </FooterItemLayout>
  );
};

const More = () => {
  return (
    <div className="flex flex-col h-full w-full justify-between md:items-end md:text-end min-h-20">
      <FooterLink href="mailto:lalouiseprinter@gmail.com" children="Suporte" />
      <div className="flex flex-col gap-2">
        <span className="text-xs opacity-90 text-muted-foreground">
          © 2026 Lalouise Consultoria Sanitária.
        </span>
        <span className="text-xs opacity-50 text-muted-foreground">
          Desenvolvido por Matheus Fraga.
        </span>
      </div>
    </div>
  );
};

export default function Footer() {
  return (
    <footer className="w-full py-6">
      <div className="w-full max-w-7xl m-auto p-4 grid grid-cols-1 md:grid-cols-4 gap-8 items-start md:justify-items-center">
        <About />
        <Nav />
        <Contact />
        <More />
      </div>
    </footer>
  );
}
