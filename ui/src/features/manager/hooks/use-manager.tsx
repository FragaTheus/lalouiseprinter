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

interface CreateManagerRequest {
  nickname: string;
  email: string;
  password: string;
  confirmPassword: string;
  restaurantId: string;
}

export const useCreateManager = () => {
  const { push } = useRouter();
  return useMutation({
    mutationFn: async (data: CreateManagerRequest) => {
      const response = await api.post("/api/v1/managers", data);
      return response.data;
    },
    onSuccess: (id) => {
      toast.success("Novo gerente registrado com sucesso!", {
        action: {
          label: "Ver gerente",
          onClick: () => push(`/dashboard/managers/${id}`),
        },
      });
      push("/dashboard/managers");
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

export const useManagerListInfinite = (params?: ManagerListRequest) => {
  return useInfiniteQuery({
    queryKey: ["managers", "list", "infinite", params],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<ManagerSummary>>("/api/v1/managers", {
        params: { ...params, page: pageParam },
      });
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

export const useManagerInfo = (targetId: string) => {
  return useQuery({
    queryKey: ["managers", "info", targetId],
    queryFn: async () => {
      const response = await api.get<ManagerInfo>(
        `/api/v1/managers/${targetId}`,
      );
      return response.data;
    },
  });
};

interface ChangeManagerNicknameRequest {
  newNickname: string;
}

export const useManagerChangeName = (targetId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (data: ChangeManagerNicknameRequest) => {
      await api.patch(`/api/v1/managers/${targetId}/change-name`, data);
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

export const useManagerChangePassword = (targetId: string) => {
  return useMutation({
    mutationFn: async (data: ManagerChangePasswordRequest) => {
      await api.patch(`/api/v1/managers/${targetId}/change-password`, data);
    },
    onSuccess: () => {
      toast.success("Senha do gerente atualizada com sucesso!");
    },
  });
};

export const useDeactivateManager = (targetId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      await api.delete(`/api/v1/managers/${targetId}`);
    },
    onSuccess: () => {
      toast.success("Gerente desativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["managers"] });
    },
  });
};

export const useReactivateManager = (targetId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      await api.patch(`/api/v1/managers/${targetId}/reactivate`);
    },
    onSuccess: () => {
      toast.success("Gerente reativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["managers"] });
    },
  });
};
