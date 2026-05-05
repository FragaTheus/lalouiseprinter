import { cn } from "@/lib/utils";
import Link from "next/link";

export default function AppLogo({
  className,
  href = "/",
}: {
  className?: string;
  href?: string;
}) {
  return (
    <Link
      href={href}
      className={cn(
        "flex items-center gap-2 font-black tracking-tighter text-2xl",
        className,
      )}
    >
      LaLouise
    </Link>
  );
}
