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

interface CreateStaffRequest {
  nickname: string;
  email: string;
  password: string;
  confirmPassword: string;
  sectorId: string;
}

export const useCreateStaff = () => {
  const { push } = useRouter();
  const { id: restaurantId, sectorId } = useParams<{
    id: string;
    sectorId: string;
  }>();
  const base = `/dashboard/restaurants/${restaurantId}/resources/staffs`;
  return useMutation({
    mutationFn: async (data: CreateStaffRequest) => {
      const response = await api.post(
        `/api/v1/restaurants/${restaurantId}/staffs`,
        data,
      );
      console.log("CreateStaffResponse:", response.data);
      return response.data;
    },
    onSuccess: (id) => {
      toast.success("Novo colaborador registrado com sucesso!", {
        action: {
          label: "Ver colaborador",
          onClick: () => push(`${base}/${id}`),
        },
      });
      push(base);
    },
  });
};

interface StaffListRequest {
  term?: string;
  active?: boolean;
}

interface StaffSummary {
  id: string;
  nickname: string;
  email: string;
  active: boolean;
}

export const useStaffListInfinite = (
  restaurantId: string,
  sectorId: string,
  params?: StaffListRequest,
) => {
  return useInfiniteQuery({
    queryKey: ["staffs", "list", "infinite", restaurantId, sectorId, params],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<StaffSummary>>(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/staffs`,
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

export const useStaffListByRestaurantInfinite = (
  restaurantId: string,
  params?: StaffListRequest,
) => {
  return useInfiniteQuery({
    queryKey: [
      "staffs",
      "list",
      "infinite",
      "restaurant",
      restaurantId,
      params,
    ],
    queryFn: async ({ pageParam = 0 }) => {
      const response = await api.get<Page<StaffSummary>>(
        `/api/v1/restaurants/${restaurantId}/staffs`,
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

interface StaffInfo {
  id: string;
  nickname: string;
  email: string;
  role: string;
  active: boolean;
  restaurantName: string;
  sectorName: string;
  createdAt: string;
  updatedAt: string;
}

export const useStaffInfo = (
  restaurantId: string,
  sectorId: string,
  targetId: string,
) => {
  return useQuery({
    queryKey: ["staffs", "info", restaurantId, sectorId, targetId],
    queryFn: async () => {
      const response = await api.get<StaffInfo>(
        `/api/v1/restaurants/${restaurantId}/staffs/${targetId}`,
      );
      return response.data;
    },
  });
};

interface ChangeStaffNicknameRequest {
  newNickname: string;
}

export const useStaffChangeName = (
  restaurantId: string,
  sectorId: string,
  targetId: string,
) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (data: ChangeStaffNicknameRequest) => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/staffs/${targetId}/change-name`,
        data,
      );
    },
    onSuccess: () => {
      toast.success("Nome do colaborador atualizado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["staffs"] });
    },
  });
};

interface StaffChangePasswordRequest {
  newPassword: string;
  confirmNewPassword: string;
}

export const useStaffChangePassword = (
  restaurantId: string,
  sectorId: string,
  targetId: string,
) => {
  return useMutation({
    mutationFn: async (data: StaffChangePasswordRequest) => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/staffs/${targetId}/change-password`,
        data,
      );
    },
    onSuccess: () => {
      toast.success("Senha do colaborador atualizada com sucesso!");
    },
  });
};

export const useDeactivateStaff = (
  restaurantId: string,
  sectorId: string,
  targetId: string,
) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      await api.delete(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/staffs/${targetId}`,
      );
    },
    onSuccess: () => {
      toast.success("Colaborador desativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["staffs"] });
    },
  });
};

export const useReactivateStaff = (
  restaurantId: string,
  sectorId: string,
  targetId: string,
) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      await api.patch(
        `/api/v1/restaurants/${restaurantId}/sectors/${sectorId}/staffs/${targetId}/reactivate`,
      );
    },
    onSuccess: () => {
      toast.success("Colaborador reativado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["staffs"] });
    },
  });
};
