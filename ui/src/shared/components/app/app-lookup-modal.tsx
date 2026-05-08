"use client";

interface LookupOption {
  restaurantId: string;
  name: string;
}

interface AppLookupModalProps {
  label: string;
  name: string;
  placeholder?: string;
  options: LookupOption[][] | undefined;
  onSearch: (value: string) => void;
  isLoading?: boolean;
  hasNextPage?: boolean;
  fetchNextPage: () => void;
  isFetchingNextPage?: boolean;
}

import { useEffect, useRef, useState } from "react";
import { useFormContext } from "react-hook-form";
import { Field, FieldContent, FieldLabel } from "../ui/field";
import { Button } from "../ui/button";
import {
  Command,
  CommandDialog,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "../ui/command";
import { AiOutlineLoading } from "react-icons/ai";
import { useDebouncedCallback } from "use-debounce";

export default function AppLookupModal(props: AppLookupModalProps) {
  const { setValue } = useFormContext();
  const [open, setOpen] = useState(false);
  const [search, setSearch] = useState("");
  const [selected, setSelected] = useState<LookupOption | undefined>(undefined);

  const handleSearch = useDebouncedCallback((value: string) => {
    props.onSearch(value);
  }, 300);

  function handleInputChange(value: string) {
    setSearch(value);
    handleSearch(value);
  }

  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!bottomRef.current) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (
          entries[0].isIntersecting &&
          props.hasNextPage &&
          !props.isFetchingNextPage
        ) {
          props.fetchNextPage();
        }
      },
      { threshold: 1.0 },
    );

    observer.observe(bottomRef.current);
    return () => observer.disconnect();
  }, [props.hasNextPage, props.isFetchingNextPage]);

  return (
    <Field>
      <FieldLabel>{props.label}</FieldLabel>
      <FieldContent>
        <Button type="button" variant="outline" onClick={() => setOpen(true)}>
          {selected ? selected.name : props.placeholder}
        </Button>
      </FieldContent>

      <CommandDialog open={open} onOpenChange={setOpen}>
        <Command>
          <CommandInput
            placeholder={props.placeholder}
            value={search}
            onValueChange={handleInputChange}
          />
          <CommandList>
            <CommandGroup heading={props.label}>
              {props.options?.map((page) =>
                page.map((opt) => (
                  <CommandItem
                    value={opt.name}
                    key={opt.restaurantId}
                    onSelect={() => {
                      setSelected(opt);
                      setValue(props.name, opt.restaurantId);
                      setOpen(false);
                      setSearch("");
                    }}
                  >
                    {opt.name}
                  </CommandItem>
                )),
              )}
              <div ref={bottomRef} className="py-1">
                {props.isFetchingNextPage && (
                  <div className="flex justify-center">
                    <AiOutlineLoading className="animate-spin text-xl" />
                  </div>
                )}
              </div>
            </CommandGroup>
          </CommandList>
        </Command>
      </CommandDialog>
    </Field>
  );
}
