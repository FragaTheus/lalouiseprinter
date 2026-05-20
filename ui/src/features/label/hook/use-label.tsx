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

// ─── Print ─────────────────────────────────────────────────────────────────

interface PrintLabelRequest {
  productId: string;
  sectorId?: string;
  storage: LabelStorageType;
  copies: number;
}

export const usePrintLabel = (restaurantId: string, sectorId?: string) => {
  const { push } = useRouter();

  return useMutation({
    mutationFn: async (data: PrintLabelRequest) => {
      const resolvedSectorId = sectorId ?? data.sectorId;

      if (!resolvedSectorId) {
        throw new Error("sectorId is required");
      }

      const response = await api.post(
        `/api/v1/restaurants/${restaurantId}/sectors/${resolvedSectorId}/labels/print`,
        data,
      );
      return { id: response.data as string, resolvedSectorId };
    },
    onSuccess: ({ id, resolvedSectorId }) => {
      toast.success("Etiqueta impressa com sucesso!", {
        action: {
          label: "Ver etiqueta",
          onClick: () =>
            push(
              `/dashboard/restaurants/${restaurantId}/resources/labels/${id}`,
            ),
        },
      });

      const redirectPath = sectorId
        ? `/dashboard/restaurants/${restaurantId}/resources/sectors/${resolvedSectorId}/resources/labels`
        : `/dashboard/restaurants/${restaurantId}/resources/labels`;

      push(redirectPath);
    },
  });
};

// ─── Reprint by sector context (STAFF — sectorId from JWT) ─────────────────

interface ReprintLabelRequest {
  sectorId?: string;
  storage: LabelStorageType;
  copies: number;
}

export const useReprintLabel = (
  restaurantId: string,
  labelId: string,
  sectorId?: string,
) => {
  const queryClient = useQueryClient();
  const { push } = useRouter();

  return useMutation({
    mutationFn: async (data: ReprintLabelRequest) => {
      const resolvedSectorId = sectorId ?? data.sectorId;

      if (!resolvedSectorId) {
        throw new Error("sectorId is required");
      }

      const response = await api.post(
        `/api/v1/restaurants/${restaurantId}/sectors/${resolvedSectorId}/labels/${labelId}/reprint`,
        data,
      );
      return response.data as string;
    },
    onSuccess: (id: string) => {
      toast.success("Etiqueta impressa com sucesso!", {
        action: {
          label: "Ver etiqueta",
          onClick: () =>
            push(
              `/dashboard/restaurants/${restaurantId}/resources/labels/${id}`,
            ),
        },
      });
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
