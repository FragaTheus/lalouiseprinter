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

export type LabelStatus = "ACTIVE" | "EXPIRING" | "EXPIRED" | "DISCARDED";
export type LabelStorageType =
  | "AMBIENT"
  | "REFRIGERATED"
  | "FROZEN"
  | "DEEP_FROZEN";

export const STATUS_LABELS: Record<LabelStatus, string> = {
  ACTIVE: "Ativo",
  EXPIRING: "Vencendo",
  EXPIRED: "Vencido",
  DISCARDED: "Descartado",
};

export const STATUS_COLORS: Record<LabelStatus, string> = {
  ACTIVE: "bg-green-500",
  EXPIRING: "bg-yellow-400",
  EXPIRED: "bg-destructive",
  DISCARDED: "bg-gray-400",
};

export interface LabelSummary {
  id: string;
  productName: string;
  sectorName: string;
  lot: string;
  validateDate: string;
  status: LabelStatus;
}

export interface LabelInfo {
  id: string;
  restaurantName: string;
  sectorName: string;
  productName: string;
  printedBy: string;
  lot: string;
  validateDate: string;
  createdAt: string;
  updateAt: string;
  status: LabelStatus;
}

interface PrintLabelRequest {
  productId: string;
  sectorId?: string;
  storage: LabelStorageType;
  copies: number;
}

interface ReprintLabelRequest {
  storage: LabelStorageType;
}

// ─── Print ─────────────────────────────────────────────────────────────────

export const usePrintLabelInSectorContext = (
  restaurantId: string,
  sectorId: string,
) => {
  const { push } = useRouter();
  return useMutation({
    mutationFn: async (data: PrintLabelRequest) => {
      const response = await api.post(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/labels/print`,
        data,
      );
      return response.data as string;
    },
    onSuccess: (id) => {
      toast.success("Etiqueta impressa com sucesso!", {
        action: {
          label: "Ver etiqueta",
          onClick: () =>
            push(
              `/dashboard/restaurants/${restaurantId}/resources/labels/${id}`,
            ),
        },
      });
      push(
        `/dashboard/restaurants/${restaurantId}/resources/sectors/${sectorId}/resources/labels`,
      );
    },
  });
};

export const usePrintLabelByInputSector = (restaurantId: string) => {
  const { push } = useRouter();
  return useMutation({
    mutationFn: async (data: PrintLabelRequest) => {
      const response = await api.post(
        `/api/v1/restaurants/${restaurantId}/sectors/${data.sectorId}/labels/print`,
        data,
      );
      return response.data as string;
    },
    onSuccess: (id) => {
      toast.success("Etiqueta impressa com sucesso!", {
        action: {
          label: "Ver etiqueta",
          onClick: () =>
            push(
              `/dashboard/restaurants/${restaurantId}/resources/labels/${id}`,
            ),
        },
      });
      push(`/dashboard/restaurants/${restaurantId}/resources/labels`);
    },
  });
};

// ─── Reprint by sector context (STAFF — sectorId from JWT) ─────────────────

export const useReprintBySectorContext = (
  restaurantId: string,
  sectorId: string,
  labelId: string,
) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (data: ReprintLabelRequest) => {
      const response = await api.post(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/labels/${labelId}/reprint`,
        data,
      );
      return response.data as string;
    },
    onSuccess: () => {
      toast.success("Etiqueta reimpressa com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["labels"] });
    },
  });
};

// ─── Reprint by input sector (MANAGER / ADMIN) ─────────────────────────────

export const useReprintByInputSector = (
  restaurantId: string,
  labelId: string,
  sectorId: string,
) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (data: ReprintLabelRequest) => {
      const response = await api.post(
        `/api/v1/restaurants/${restaurantId}/labels/${labelId}/reprint/${sectorId}`,
        data,
      );
      return response.data as string;
    },
    onSuccess: () => {
      toast.success("Etiqueta reimpressa com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["labels"] });
    },
  });
};

// ─── Get label info ────────────────────────────────────────────────────────

export const useLabelInfo = (restaurantId: string, targetId: string) => {
  return useQuery({
    queryKey: ["labels", "info", restaurantId, targetId],
    queryFn: async () => {
      const response = await api.get<LabelInfo>(
        `/api/v1/restaurants/${restaurantId}/labels/${targetId}`,
      );
      return response.data;
    },
  });
};

// ─── List with infinite scroll ─────────────────────────────────────────────

interface LabelListParams {
  term?: string;
  status?: LabelStatus;
}

export const useLabelListInfinite = (
  restaurantId: string,
  sectorId: string,
  params?: LabelListParams,
) => {
  return useInfiniteQuery({
    queryKey: ["labels", "list", "infinite", restaurantId, params],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<LabelSummary>>(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/labels`,
        { params: { ...params, page: pageParam } },
      );
      return response.data;
    },
    getNextPageParam: (lastPage) => {
      if (lastPage.last) return undefined;
      return lastPage.number + 1;
    },
  });
};

// ─── Search by restaurant (global monitoring) ──────────────────────────────

export const useSearchLabelsByRestaurant = (
  restaurantId: string,
  params?: LabelListParams,
) => {
  return useInfiniteQuery({
    queryKey: ["labels", "search", "restaurant", restaurantId, params],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<LabelSummary>>(
        `/api/v1/restaurants/${restaurantId}/labels/search`,
        { params: { ...params, page: pageParam } },
      );
      return response.data;
    },
    getNextPageParam: (lastPage) => {
      if (lastPage.last) return undefined;
      return lastPage.number + 1;
    },
  });
};

// ─── Search by lot ─────────────────────────────────────────────────────────

export const useSearchLabelsByLot = (restaurantId: string, lotCode: string) => {
  return useInfiniteQuery({
    queryKey: ["labels", "search", "lot", restaurantId, lotCode],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<LabelSummary>>(
        `/api/v1/restaurants/${restaurantId}/labels/search/lot`,
        { params: { lotCode, page: pageParam } },
      );
      console.log(response.data);
      return response.data;
    },
    getNextPageParam: (lastPage) => {
      if (lastPage.last) return undefined;
      return lastPage.number + 1;
    },
  });
};
