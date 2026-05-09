"use client";

import { useController, useFormContext } from "react-hook-form";
import {
  STORAGE_OPTIONS,
  StorageType,
} from "@/features/sector/hook/use-sector";
import { Field, FieldContent, FieldLabel } from "../ui/field";
import { Checkbox } from "../ui/checkbox";
import { Label } from "../ui/label";

interface AppStorageSelectProps {
  name: string;
  label?: string;
}

export default function AppStorageSelect({
  name,
  label = "Armazenamentos",
}: AppStorageSelectProps) {
  const { control } = useFormContext();
  const { field } = useController({
    name,
    control,
    defaultValue: [],
  });

  const selected: StorageType[] = field.value ?? [];

  function toggle(value: StorageType) {
    const next = selected.includes(value)
      ? selected.filter((v) => v !== value)
      : [...selected, value];
    field.onChange(next);
  }

  return (
    <Field>
      <FieldLabel>{label}</FieldLabel>
      <FieldContent>
        <div className="flex flex-wrap gap-4">
          {STORAGE_OPTIONS.map((opt) => (
            <div key={opt.value} className="flex items-center gap-2">
              <Checkbox
                id={`storage-${opt.value}`}
                checked={selected.includes(opt.value)}
                onCheckedChange={() => toggle(opt.value)}
              />
              <Label htmlFor={`storage-${opt.value}`}>{opt.label}</Label>
            </div>
          ))}
        </div>
      </FieldContent>
    </Field>
  );
}
