"use client";

import { Button } from "@/shared/components/ui/button";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "../ui/alert-dialog";

interface ActionConfig {
  text: string;
  mutate: () => void;
}

export interface AppToggleActivationProps {
  isActive?: boolean;
  active: ActionConfig;
  inactive: ActionConfig;
}

export default function AppToggleActivation({
  isActive,
  active,
  inactive,
}: AppToggleActivationProps) {
  const action = isActive ? active : inactive;
  const variant = isActive ? "destructive" : "default";

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button variant={variant}>{action.text}</Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogTitle>Você tem certeza?</AlertDialogTitle>
        <AlertDialogDescription>
          Essa ação irá {isActive ? "desativar" : "reativar"} o registro.
        </AlertDialogDescription>
        <AlertDialogFooter>
          <AlertDialogCancel>Cancelar</AlertDialogCancel>
          <AlertDialogAction variant={variant} onClick={action.mutate}>
            Confirmar
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
