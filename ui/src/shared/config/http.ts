import axios, { AxiosError } from "axios";
import { toast } from "sonner";

type ApiErrorResponse = {
	error?: string;
};

const DEFAULT_API_BASE_URL = "http://localhost:8080";
const isBrowser = typeof window !== "undefined";

const readAuthorizationHeader = (headers?: Record<string, unknown>) => {
	if (!headers) {
		return null;
	}

	const value = headers.authorization ?? headers.Authorization;

	if (typeof value === "string" && value.trim().length > 0) {
		return value;
	}

	if (Array.isArray(value) && typeof value[0] === "string") {
		return value[0];
	}

	return null;
};

export const api = axios.create({
	baseURL: DEFAULT_API_BASE_URL,
	headers: {
		Accept: "application/json",
		"Content-Type": "application/json",
	},
});

api.interceptors.request.use((config) => {
	if (!isBrowser) {
		return config;
	}

	const token = localStorage.getItem("token");

	if (token) {
		config.headers.Authorization = `${token}`;
	}

	return config;
});

api.interceptors.response.use(
	(response) => {
		const token = readAuthorizationHeader(
			response.headers as Record<string, unknown>,
		);

		if (token && isBrowser) {
			localStorage.setItem("token", token);
		}

		return response;
	},
	(error: AxiosError<ApiErrorResponse>) => {
		const errorMessage =
			error.response?.data?.error || "Ocorreu um erro na requisição";

		if (
			isBrowser &&
			error.response?.status === 401 &&
			window.location.pathname !== "/auth/login"
		) {
			localStorage.removeItem("token");
			toast.error("Sessão expirada. Faça login novamente.");
			window.location.href = "/auth/login";
		}

		if (isBrowser && error.response?.status === 403) {
			toast.error("Acesso negado. Você não tem permissão para acessar este recurso.");
			window.location.href = "/forbidden";
		}

		if (isBrowser) {
			toast.error(errorMessage);
		}

		return Promise.reject(new Error(errorMessage));
	},
);