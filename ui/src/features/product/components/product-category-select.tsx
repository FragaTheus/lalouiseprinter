import { useFormContext } from "react-hook-form";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/components/ui/select";

const CATEGORIES = [
  { value: "PROTEIN", label: "Proteína" },
  { value: "SEAFOOD", label: "Fruto do Mar" },
  { value: "VEGETABLE", label: "Vegetal" },
  { value: "GRAINS", label: "Grãos" },
  { value: "PASTA", label: "Massas" },
  { value: "SEASONINGS", label: "Temperos" },
  { value: "SAUCES", label: "Molhos" },
  { value: "OILS", label: "Óleos" },
];

interface CategorySelectProps {
  name: string;
  placeholder?: string;
}

export function CategorySelect({
  name,
  placeholder = "Selecione uma categoria",
}: CategorySelectProps) {
  const { register, setValue } = useFormContext();
  const { ref } = register(name);

  return (
    <Select onValueChange={(value) => setValue(name, value)}>
      <SelectTrigger ref={ref}>
        <SelectValue placeholder={placeholder} />
      </SelectTrigger>
      <SelectContent>
        <SelectGroup>
          {CATEGORIES.map((category) => (
            <SelectItem key={category.value} value={category.value}>
              {category.label}
            </SelectItem>
          ))}
        </SelectGroup>
      </SelectContent>
    </Select>
  );
}
