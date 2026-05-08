import { create } from "zustand";
import { createJSONStorage, persist, type StateStorage } from "zustand/middleware";

const TOKEN_STORAGE_KEY = "token";

export interface AuthUser {
  id: string;
  nickname: string;
  email: string;
  role: string;
  restaurantId?: string;
}

interface UserStoreState {
  user: AuthUser | null;
  isAuthenticated: boolean;
  setUser: (user: AuthUser) => void;
  clearUser: () => void;
  logout: () => void;
}

const noopStorage: StateStorage = {
  getItem: () => null,
  setItem: () => {},
  removeItem: () => {},
};

export const useUserStore = create<UserStoreState>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,
      setUser: (user) => {
        set({ user, isAuthenticated: true });
      },
      clearUser: () => {
        set({ user: null, isAuthenticated: false });
      },
      logout: () => {
        set({ user: null, isAuthenticated: false });

        if (typeof window !== "undefined") {
          localStorage.removeItem(TOKEN_STORAGE_KEY);
        }
      },
    }),
    {
      name: "user-auth-storage",
      storage: createJSONStorage(() =>
        typeof window !== "undefined" ? localStorage : noopStorage,
      ),
      partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    },
  ),
);
