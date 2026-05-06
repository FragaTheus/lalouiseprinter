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

interface RegisterAdminRequest {
  nickname: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export const useRegisterAdmin = () => {
  const { push } = useRouter();
  return useMutation({
    mutationFn: async (data: RegisterAdminRequest) => {
      const response = await api.post("/api/v1/admins", data);
      return response.data;
    },
    onSuccess: (id) => {
      toast.success("Novo administrador registrado com sucesso!", {
        action: {
          label: "Ver administrador",
          onClick: () => push(`/dashboard/admins/${id}`),
        },
      });
      push(`/dashboard/admins`);
    },
  });
};

interface AdminListRequest {
  term?: string;
  active?: boolean;
  page?: number;
  size?: number;
}

interface AdminSummary {
  id: string;
  nickname: string;
  email: string;
  active: boolean;
}

export const useAdminListInfinite = (
  params?: Omit<AdminListRequest, "page">,
) => {
  return useInfiniteQuery({
    queryKey: ["admins", "list", "infinite", params],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<AdminSummary>>("/api/v1/admins", {
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

interface AdminInfo {
  id: string;
  nickname: string;
  email: string;
  createdAt: string;
  active: boolean;
  updatedAt: string;
}

export const useAdminInfo = (targetId: string) => {
  return useQuery({
    queryKey: ["admins", "info", targetId],
    queryFn: async () => {
      const response = await api.get<AdminInfo>(`/api/v1/admins/${targetId}`);
      return response.data;
    },
  });
};

interface AdminChangeNameRequest {
  newNickname: string;
}

export const useAdminChangeName = (targetId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: AdminChangeNameRequest) => {
      await api.patch(`/api/v1/admins/${targetId}/change-name`, data);
    },
    onSuccess: () => {
      toast.success("Nome do administrador atualizado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["admins"] });
    },
  });
};

interface AdminChangePasswordRequest {
  newPassword: string;
  confirmNewPassword: string;
}

export const useAdminChangePassword = (targetId: string) => {
  return useMutation({
    mutationFn: async (data: AdminChangePasswordRequest) => {
      await api.patch(`/api/v1/admins/${targetId}/change-password`, data);
    },
    onSuccess: () => {
      toast.success("Senha do administrador atualizada com sucesso!");
    },
  });
};

export const useDeactiveAdmin = (targetId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      await api.delete(`/api/v1/admins/${targetId}`);
    },
    onSuccess: () => {
      toast.success("Administrador desativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["admins"] });
    },
  });
};

export const useReactiveAdmin = (targetId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      await api.patch(`/api/v1/admins/${targetId}/reactivate`);
    },
    onSuccess: () => {
      toast.success("Administrador reativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["admins"] });
    },
  });
};
