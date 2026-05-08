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

interface CreateProductRequest {
  name: string;
  description: string;
}

export const useCreateProduct = (restaurantId: string) => {
  const { push } = useRouter();
  return useMutation({
    mutationFn: async (data: CreateProductRequest) => {
      const response = await api.post(
        `/api/v1/restaurants/${restaurantId}/products`,
        data,
      );
      return response.data;
    },
    onSuccess: (id) => {
      toast.success("Novo produto registrado com sucesso!", {
        action: {
          label: "Ver produto",
          onClick: () =>
            push(
              `/dashboard/restaurants/${restaurantId}/resources/products/${id}`,
            ),
        },
      });
      push(`/dashboard/restaurants/${restaurantId}/resources/products`);
    },
  });
};

interface ProductListRequest {
  term?: string;
  active?: boolean;
  size?: number;
}

interface ProductSummary {
  id: string;
  name: string;
  description: string;
  active: boolean;
  restaurantId: string;
}

export const useProductListInfinite = (
  restaurantId: string,
  params?: ProductListRequest,
) => {
  return useInfiniteQuery({
    queryKey: ["products", "list", "infinite", restaurantId, params],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<ProductSummary>>(
        `/api/v1/restaurants/${restaurantId}/products`,
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

interface ProductInfo {
  id: string;
  name: string;
  description: string;
  active: boolean;
  restaurantId: string;
  createdAt: string;
  updatedAt: string;
}

export const useProductInfo = (restaurantId: string, productId: string) => {
  return useQuery({
    queryKey: ["products", "info", restaurantId, productId],
    queryFn: async () => {
      const response = await api.get<ProductInfo>(
        `/api/v1/restaurants/${restaurantId}/products/${productId}`,
      );
      return response.data;
    },
  });
};

interface UpdateProducName {
  newProductName: string;
}

export const useProductChangeName = (
  restaurantId: string,
  targetId: string,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: UpdateProducName) => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/products/${targetId}/change-name`,
        data,
      );
    },
    onSuccess: () => {
      toast.success("Nome do produto atualizado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });
};

interface UpdateProductDescription {
  newDescription: string;
}

export const useUpdateProductDescription = (
  restaurantId: string,
  targetId: string,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: UpdateProductDescription) => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/products/${targetId}/change-description`,
        data,
      );
    },
    onSuccess: () => {
      toast.success("Descrição do produto atualizada com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });
};

export const useDeleteProduct = (restaurantId: string, productId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      await api.delete(
        `/api/v1/restaurants/${restaurantId}/resources/products/${productId}`,
      );
    },
    onSuccess: () => {
      toast.success("Produto desativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });
};

export const useReactivateProduct = (
  restaurantId: string,
  productId: string,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/resources/products/${productId}/reactivate`,
      );
    },
    onSuccess: () => {
      toast.success("Produto reativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });
};
