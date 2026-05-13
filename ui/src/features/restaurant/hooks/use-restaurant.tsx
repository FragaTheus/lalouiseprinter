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

interface CreateRestaurantRequest {
  restaurantName: string;
  cnpj: string;
}

export const useCreateRestaurant = () => {
  const { push } = useRouter();
  return useMutation({
    mutationFn: async (data: CreateRestaurantRequest) => {
      const response = await api.post("/api/v1/restaurants", data);
      return response.data;
    },
    onSuccess: (id) => {
      toast.success("Novo restaurante registrado com sucesso!", {
        action: {
          label: "Ver restaurante",
          onClick: () => push(`/dashboard/restaurants/${id}/info`),
        },
      });
      push("/dashboard/restaurants");
    },
  });
};

interface RestaurantListRequest {
  term?: string;
  active?: boolean;
}

interface RestaurantSummary {
  restaurantId: string;
  restaurantName: string;
  active: boolean;
}

export const useRestaurantListInfinite = (params?: RestaurantListRequest) => {
  return useInfiniteQuery({
    queryKey: ["restaurants", "list", "infinite", params],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<RestaurantSummary>>(
        "/api/v1/restaurants",
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

interface RestaurantInfo {
  id: string;
  name: string;
  cnpj: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export const useRestaurantInfo = (restaurantId: string) => {
  return useQuery({
    queryKey: ["restaurants", "info", restaurantId],
    queryFn: async () => {
      const response = await api.get<RestaurantInfo>(
        `/api/v1/restaurants/${restaurantId}`,
      );
      return response.data;
    },
  });
};

interface ChangeRestaurantNameRequest {
  restaurantName: string;
}

export const useChangeRestaurantName = (restaurantId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: ChangeRestaurantNameRequest) => {
      await api.patch(`/api/v1/restaurants/${restaurantId}`, data);
    },
    onSuccess: () => {
      toast.success("Nome do restaurante atualizado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["restaurants"] });
    },
  });
};

export const useDeleteRestaurant = (restaurantId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      await api.delete(`/api/v1/restaurants/${restaurantId}`);
    },
    onSuccess: () => {
      toast.success("Restaurante desativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["restaurants"] });
    },
  });
};

export const useReactiveRestaurant = (restaurantId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      await api.patch(`/api/v1/restaurants/${restaurantId}/reactive`);
    },
    onSuccess: () => {
      toast.success("Restaurante reativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["restaurants"] });
    },
  });
};

interface RestaurantLookUpParams {
  term?: string;
}

interface RestaurantLookUp {
  restaurantId: string;
  restaurantName: string;
}

export const useRestaurantLookUp = (params?: RestaurantLookUpParams) => {
  return useInfiniteQuery({
    queryKey: ["restaurants", "lookup", "infinite", params],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<RestaurantLookUp>>(
        "/api/v1/restaurants/lookup",
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

interface RestaurantLookUpInfoResponse {
  restaurantName: string;
}

export const useRestaurantLookUpInfo = (restaurantId: string) => {
  return useQuery({
    queryKey: ["restaurants", "lookup", restaurantId],
    queryFn: async () => {
      const response = await api.get<RestaurantLookUpInfoResponse>(
        `/api/v1/restaurants/${restaurantId}/lookup`,
      );
      return response.data;
    },
  });
};
