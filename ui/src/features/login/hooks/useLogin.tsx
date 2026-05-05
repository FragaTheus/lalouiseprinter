"use client";

import { api } from "@/shared/config/http";
import { useUserStore } from "@/store/user-store";
import { useRouter } from "next/navigation";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  id: string;
  nickname: string;
  email: string;
  role: string;
}

export default function useLogin() {
  const { push } = useRouter();
  const setUser = useUserStore((state) => state.setUser);

  return useMutation({
    mutationFn: async (data: LoginRequest): Promise<LoginResponse> => {
      const response = await api.post<LoginResponse>(
        "/api/v1/auth/login",
        data,
      );
      return response.data;
    },
    onSuccess: (data: LoginResponse) => {
      toast.success("Que bom ter voce de volta! " + data.nickname);
      setUser(data);
      push("/dashboard");
    },
  });
}

export function useLogout() {
  const { push } = useRouter();
  const queryClient = useQueryClient();
  const logout = useUserStore((state) => state.logout);

  return useMutation({
    mutationFn: async () => {
      logout();
    },
    onSuccess: async () => {
      await queryClient.cancelQueries();
      queryClient.clear();
      push("/auth/login");
      toast.success("Logout realizado com sucesso!");
    },
  });
}
