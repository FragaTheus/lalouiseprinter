import About from "@/features/home/about";
import Cta from "@/features/home/cta";
import Footer from "@/features/home/footer";
import HomeHeader from "@/features/home/home-header";
import HomeHero from "@/features/home/home-hero";
import HowTo from "@/features/home/how-to";
import Printer from "@/features/home/printer";
import SocialProof from "@/features/home/social-proof";
import Solutions from "@/features/home/solutions";

export default function Home() {
  return (
    <main>
      <HomeHeader />
      <HomeHero />
      <Solutions />
      <Printer />
      <HowTo />
      <About />
      <Cta />
      <Footer />
    </main>
  );
}
