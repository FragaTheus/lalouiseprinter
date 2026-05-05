"use client";

import { FormProvider, useForm, useFormContext } from "react-hook-form";
import { Field, FieldGroup } from "../ui/field";
import { Button } from "../ui/button";
import { AiOutlineLoading } from "react-icons/ai";

export default function AppForm({
  children,
  btnText,
  onSubmit,
  isPending,
}: {
  children: React.ReactNode;
  btnText: string;
  onSubmit: (data: any) => void;
  isPending?: boolean;
}) {
  const form = useForm();
  return (
    <FormProvider {...form}>
      <form className="max-w-sm" onSubmit={form.handleSubmit(onSubmit)}>
        <FieldGroup>
          {children}
          <Field>
            <Button type="submit" disabled={isPending}>
              {isPending ? (
                <AiOutlineLoading className="animate-spin" />
              ) : (
                btnText
              )}
            </Button>
          </Field>
        </FieldGroup>
      </form>
    </FormProvider>
  );
}
