import {
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card";
import { Label } from "@/shared/components/ui/label";

export default function LoginCardHeader() {
  return (
    <CardHeader>
      <Label>Bem vindo</Label>
      <CardTitle>Acesse sua conta.</CardTitle>
      <CardDescription>
        Entre com seus dados para acessar mais recursos
      </CardDescription>
    </CardHeader>
  );
}
