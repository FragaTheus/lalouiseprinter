"use client";

import { useFormContext } from "react-hook-form";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/components/ui/select";
import { useSectorStorages } from "@/features/sector/hook/use-sector";
import { AiOutlineLoading } from "react-icons/ai";

const STORAGE_LABELS: Record<string, string> = {
  AMBIENT: "Ambiente",
  REFRIGERATED: "Refrigerado",
  FROZEN: "Congelado",
  DEEP_FROZEN: "Ultra-congelado",
};

interface LabelStorageSelectProps {
  name: string;
  sectorId?: string;
  restaurantId: string;
}

export function LabelStorageSelect({
  name,
  sectorId,
  restaurantId,
}: LabelStorageSelectProps) {
  const { data, isLoading } = useSectorStorages(restaurantId, sectorId, {
    enabled: !!sectorId,
  });
  const { register, setValue } = useFormContext();
  const { ref } = register(name);

  return (
    <Select
      onValueChange={(value) => setValue(name, value)}
      disabled={isLoading || !sectorId}
    >
      <SelectTrigger ref={ref}>
        {!sectorId ? (
          <SelectValue placeholder="Sem setor selecionado" />
        ) : isLoading ? (
          <AiOutlineLoading className="animate-spin duration-300" />
        ) : (
          <SelectValue placeholder="Selecione um armazenamento" />
        )}
      </SelectTrigger>
      <SelectContent>
        <SelectGroup>
          {data?.map((s) => (
            <SelectItem key={s} value={s}>
              {STORAGE_LABELS[s] || s}
            </SelectItem>
          ))}
        </SelectGroup>
      </SelectContent>
    </Select>
  );
}
