import AppHeader from "@/shared/components/app/app-header";
import Image from "next/image";
import heroImg from "@/shared/assets/hero-img.jpg";

export default function Home() {
  return (
    <main>
      <div className="absolute w-screen h-svh left-0 top-0">
        <Image
          src={heroImg}
          alt="Imagem do sushi"
          fill
          className="object-cover grayscale"
        />
        <div className="absolute inset-0 z-10 bg-linear-to-r from-background via-background/80 to-primary/50" />
      </div>

      <AppHeader>
        <p>Logo</p>
        <p>Action</p>
      </AppHeader>
    </main>
  );
}
