"use client";

import { Field, FieldContent, FieldLabel } from "@/shared/components/ui/field";
import { useFormContext, useWatch } from "react-hook-form";
import { LabelStorageSelect } from "../components/label-storage-select";

export default function LabelStorageSelectWrapper({
  restaurantId,
}: {
  restaurantId: string;
}) {
  const { control } = useFormContext();
  const sectorId = useWatch({ control, name: "sectorId" });

  return (
    <Field>
      <FieldLabel>Armazenamento</FieldLabel>
      <FieldContent>
        <LabelStorageSelect
          name="storage"
          restaurantId={restaurantId}
          sectorId={sectorId}
        />
      </FieldContent>
    </Field>
  );
}
