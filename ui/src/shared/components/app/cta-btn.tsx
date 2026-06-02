import Link from "next/link";
import { Button } from "../ui/button";
import { cn } from "@/lib/utils";

const WHATSAPP_NUMBER = "11996625687";

export default function CtaBtn({
  className,
  children,
  message,
}: {
  className?: string;
  children: React.ReactNode;
  message?: string;
}) {
  const whatsappUrl = new URL(`https://wa.me/${WHATSAPP_NUMBER}`);

  if (message) {
    whatsappUrl.searchParams.append("text", message);
  }

  return (
    <Link
      href={whatsappUrl.toString()}
      target="_blank"
      rel="noopener noreferrer"
    >
      <Button className={cn(``, className)}>{children}</Button>
    </Link>
  );
}
