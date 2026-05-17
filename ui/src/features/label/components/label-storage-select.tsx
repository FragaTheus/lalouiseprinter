import { useFormContext } from "react-hook-form";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/components/ui/select";

const STORAGES = [
  { value: "AMBIENT", label: "Ambiente" },
  { value: "REFRIGERATED", label: "Refrigerado" },
  { value: "FROZEN", label: "Congelado" },
  { value: "DEEP_FROZEN", label: "Ultra-congelado" },
];

interface LabelStorageSelectProps {
  name: string;
  placeholder?: string;
}

export function LabelStorageSelect({
  name,
  placeholder = "Selecione o tipo de armazenamento",
}: LabelStorageSelectProps) {
  const { register, setValue } = useFormContext();
  const { ref } = register(name);

  return (
    <Select onValueChange={(value) => setValue(name, value)}>
      <SelectTrigger ref={ref}>
        <SelectValue placeholder={placeholder} />
      </SelectTrigger>
      <SelectContent>
        <SelectGroup>
          {STORAGES.map((s) => (
            <SelectItem key={s.value} value={s.value}>
              {s.label}
            </SelectItem>
          ))}
        </SelectGroup>
      </SelectContent>
    </Select>
  );
}
