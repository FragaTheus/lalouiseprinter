"use client";

import { api } from "@/shared/config/http";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { toast } from "sonner";

interface ProfileResponse{
    id: string;
    nickname: string;
    email: string;
    createdAt: Date;
}

export const useProfile = () => {
    return useQuery<ProfileResponse>(
        {
            queryKey: ["profile"],
            queryFn: async () => {
                const response = await api.get<ProfileResponse>("/api/v1/me");
                return response.data;
            },
        }
    )
}

interface ChangeNameRequest{
    newNickname: string;
}

export const useProfileChangeName = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: async (data: ChangeNameRequest) => {
            await api.patch("/api/v1/me/change-name", data);
        },
        onSuccess: ()=>{
            toast.success("Novo nome salvo com sucesso!");
            queryClient.invalidateQueries({ queryKey: ["profile"] });
        }
    });
}

interface ChangePasswordRequest{
    currentPassword: string;
    newPassword: string;
    confirmNewPassword: string;
}

export const useProfileChangePassword = () => {
    return useMutation({
        mutationFn: async (data: ChangePasswordRequest) => {
            await api.patch("/api/v1/me/change-password", data);
        },
        onSuccess: ()=>{
            toast.success("Senha alterada com sucesso!");
        }
    })
}