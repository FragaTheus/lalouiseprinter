import { api } from "@/shared/config/http";
import { Page } from "@/shared/type/pagination";
import {
  useInfiniteQuery,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import { useRouter, useParams } from "next/navigation";
import { toast } from "sonner";

interface CreateManagerRequest {
  nickname: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export const useCreateManager = () => {
  const { push } = useRouter();
  const { id: restaurantId } = useParams<{ id: string }>();
  const base = `/dashboard/restaurants/${restaurantId}/resources/staffs/managers`;
  return useMutation({
    mutationFn: async (data: CreateManagerRequest) => {
      const response = await api.post(
        `/api/v1/restaurants/${restaurantId}/managers`,
        data,
      );
      return response.data;
    },
    onSuccess: (id) => {
      toast.success("Novo gerente registrado com sucesso!", {
        action: {
          label: "Ver gerente",
          onClick: () => push(`${base}/${id}`),
        },
      });
      push(`${base}/list`);
    },
  });
};

interface ManagerListRequest {
  term?: string;
  active?: boolean;
}

interface ManagerSummary {
  id: string;
  nickname: string;
  email: string;
  active: boolean;
  restaurantId: string;
}

export const useManagerListInfinite = (
  restaurantId: string,
  params?: ManagerListRequest,
) => {
  return useInfiniteQuery({
    queryKey: ["managers", "list", "infinite", restaurantId, params],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<ManagerSummary>>(
        `/api/v1/restaurants/${restaurantId}/managers`,
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

interface ManagerInfo {
  id: string;
  nickname: string;
  email: string;
  role: string;
  active: boolean;
  restaurantName: string;
  createdAt: string;
  updatedAt: string;
}

export const useManagerInfo = (restaurantId: string, targetId: string) => {
  return useQuery({
    queryKey: ["managers", "info", restaurantId, targetId],
    queryFn: async () => {
      const response = await api.get<ManagerInfo>(
        `/api/v1/restaurants/${restaurantId}/managers/${targetId}`,
      );
      return response.data;
    },
  });
};

interface ChangeManagerNicknameRequest {
  newNickname: string;
}

export const useManagerChangeName = (
  restaurantId: string,
  targetId: string,
) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (data: ChangeManagerNicknameRequest) => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/managers/${targetId}/change-name`,
        data,
      );
    },
    onSuccess: () => {
      toast.success("Nome do gerente atualizado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["managers"] });
    },
  });
};

interface ManagerChangePasswordRequest {
  newPassword: string;
  confirmNewPassword: string;
}

export const useManagerChangePassword = (
  restaurantId: string,
  targetId: string,
) => {
  return useMutation({
    mutationFn: async (data: ManagerChangePasswordRequest) => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/managers/${targetId}/change-password`,
        data,
      );
    },
    onSuccess: () => {
      toast.success("Senha do gerente atualizada com sucesso!");
    },
  });
};

export const useDeactivateManager = (
  restaurantId: string,
  targetId: string,
) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      await api.delete(
        `/api/v1/restaurants/${restaurantId}/managers/${targetId}`,
      );
    },
    onSuccess: () => {
      toast.success("Gerente desativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["managers"] });
    },
  });
};

export const useReactivateManager = (
  restaurantId: string,
  targetId: string,
) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/managers/${targetId}/reactivate`,
      );
    },
    onSuccess: () => {
      toast.success("Gerente reativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["managers"] });
    },
  });
};
