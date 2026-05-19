import { api } from "@/shared/config/http";
import { Page } from "@/shared/type/pagination";
import {
  useInfiniteQuery,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

// Mapeamento do enum Storage para exibição em português
export const STORAGE_LABELS: Record<string, string> = {
  AMBIENT: "Ambiente",
  REFRIGERATED: "Refrigerado",
  FROZEN: "Congelado",
  DEEP_FROZEN: "Ultra-congelado",
};

export type StorageType = "AMBIENT" | "REFRIGERATED" | "FROZEN" | "DEEP_FROZEN";

export const STORAGE_OPTIONS: { value: StorageType; label: string }[] = [
  { value: "AMBIENT", label: "Ambiente" },
  { value: "REFRIGERATED", label: "Refrigerado" },
  { value: "FROZEN", label: "Congelado" },
  { value: "DEEP_FROZEN", label: "Ultra-congelado" },
];

// ─── Create ────────────────────────────────────────────────────────────────

interface CreateSectorRequest {
  name: string;
  description: string;
  storages: StorageType[];
}

export const useCreateSector = (restaurantId: string) => {
  const { push } = useRouter();
  return useMutation({
    mutationFn: async (data: CreateSectorRequest) => {
      const response = await api.post(
        `/api/v1/restaurants/${restaurantId}/sectors`,
        data,
      );
      return response.data;
    },
    onSuccess: (id) => {
      toast.success("Novo setor registrado com sucesso!", {
        action: {
          label: "Ver setor",
          onClick: () =>
            push(
              `/dashboard/restaurants/${restaurantId}/resources/sectors/${id}/info`,
            ),
        },
      });
      push(`/dashboard/restaurants/${restaurantId}/resources/sectors`);
    },
  });
};

// ─── List ──────────────────────────────────────────────────────────────────

interface SectorListRequest {
  term?: string;
  active?: boolean;
  size?: number;
}

interface SectorSummary {
  id: string;
  name: string;
  active: boolean;
}

export const useSectorListInfinite = (
  restaurantId: string,
  params?: SectorListRequest,
) => {
  return useInfiniteQuery({
    queryKey: ["sectors", "list", "infinite", restaurantId, params],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<SectorSummary>>(
        `/api/v1/restaurants/${restaurantId}/sectors`,
        {
          params: { ...params, page: pageParam },
        },
      );
      return response.data;
    },
    getNextPageParam: (lastPage) => {
      if (lastPage.last) return undefined;
      return lastPage.number + 1;
    },
  });
};

// ─── Info ──────────────────────────────────────────────────────────────────

interface SectorInfo {
  id: string;
  name: string;
  description: string;
  active: boolean;
  storages: StorageType[];
  restaurantId: string;
  createdAt: string;
  updatedAt: string;
}

export const useSectorInfo = (restaurantId: string, sectorId: string) => {
  return useQuery({
    queryKey: ["sectors", "info", restaurantId, sectorId],
    queryFn: async () => {
      const response = await api.get<SectorInfo>(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}`,
      );
      return response.data;
    },
  });
};

// ─── Change Name ───────────────────────────────────────────────────────────

interface ChangeSectorNameRequest {
  newName: string;
}

export const useSectorChangeName = (restaurantId: string, sectorId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: ChangeSectorNameRequest) => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/change-name`,
        data,
      );
    },
    onSuccess: () => {
      toast.success("Nome do setor atualizado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["sectors"] });
    },
  });
};

// ─── Delete / Reactivate ───────────────────────────────────────────────────

export const useDeleteSector = (restaurantId: string, sectorId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      await api.delete(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}`,
      );
    },
    onSuccess: () => {
      toast.success("Setor desativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["sectors"] });
    },
  });
};

export const useReactivateSector = (restaurantId: string, sectorId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/reactivate`,
      );
    },
    onSuccess: () => {
      toast.success("Setor reativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["sectors"] });
    },
  });
};

// ─── Update Storages ───────────────────────────────────────────────────────

interface UpdateSectorStoragesRequest {
  storages: StorageType[];
}

export const useUpdateSectorStorages = (
  restaurantId: string,
  sectorId: string,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: UpdateSectorStoragesRequest) => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/update-storages`,
        data,
      );
    },
    onSuccess: () => {
      toast.success("Armazenamentos do setor atualizados com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["sectors"] });
    },
  });
};

// ─── Get Storages ───────────────────────────────────────────────────────

export const useSectorStorages = (
  restaurantId: string,
  sectorId?: string,
  options?: { enabled?: boolean },
) => {
  return useQuery({
    queryKey: ["sectors", "storages", restaurantId, sectorId],
    queryFn: async () => {
      const response = await api.get<StorageType[]>(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/storages`,
      );
      return response.data;
    },
    enabled: !!sectorId && (options?.enabled ?? true),
  });
};
