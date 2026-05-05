import LoginCardHeader from "@/features/login/components/login-card";
import LoginWrapper from "@/features/login/wrapper/login-wrapper";
import AppLogo from "@/shared/components/app/app-logo";
import AppRouterBack from "@/shared/components/app/app-router-back";
import AppInputPageLayout from "@/shared/components/layouts/app-input-page-layout";
import { CardContent } from "@/shared/components/ui/card";

export default function Login() {
  return (
    <AppInputPageLayout className="relative">
      <AppLogo className="absolute top-4 left-1/2 -translate-x-1/2" />
      <AppRouterBack className="absolute top-4 left-4" />
      <LoginCardHeader />
      <CardContent>
        <LoginWrapper />
      </CardContent>
    </AppInputPageLayout>
  );
}
