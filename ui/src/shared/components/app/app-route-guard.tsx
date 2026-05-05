"use client";

import { AuthUser, useUserStore } from "@/store/user-store";
import { usePathname, useRouter } from "next/navigation";
import {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";

export type AppRole = "ADMIN" | "MANAGER" | "STAFF";

const APP_ROLES: AppRole[] = ["ADMIN", "MANAGER", "STAFF"];

type GuardChildProps = {
  isAuthenticated: boolean;
  user: AuthUser | null;
  role: AppRole | null;
  roles: AppRole[];
  hasRole: (...roles: AppRole[]) => boolean;
};

type AppRouteGuardProps = {
  children: ReactNode | ((props: GuardChildProps) => ReactNode);
  allowedRoles?: AppRole[];
  loginPath?: string;
  forbiddenPath?: string;
  fallback?: ReactNode;
};

const RouteGuardContext = createContext<GuardChildProps | null>(null);

export function useRouteGuard() {
  const context = useContext(RouteGuardContext);

  if (!context) {
    throw new Error("useRouteGuard deve ser usado dentro de AppRouteGuard.");
  }

  return context;
}

const normalizeRole = (role: string | undefined): AppRole | null => {
  if (!role) {
    return null;
  }

  const normalizedRole = role.toUpperCase();

  return APP_ROLES.includes(normalizedRole as AppRole)
    ? (normalizedRole as AppRole)
    : null;
};

export default function AppRouteGuard({
  children,
  allowedRoles = APP_ROLES,
  loginPath = "/auth/login",
  forbiddenPath = "/forbidden",
  fallback = null,
}: AppRouteGuardProps) {
  const router = useRouter();
  const pathname = usePathname();
  const [isClientReady, setIsClientReady] = useState(false);

  // Use separate memoized selectors to prevent object creation on each render
  const user = useUserStore(useCallback((state) => state.user, []));
  const isAuthenticated = useUserStore(
    useCallback((state) => state.isAuthenticated, []),
  );

  useEffect(() => {
    setIsClientReady(true);
  }, []);

  const role = useMemo(() => normalizeRole(user?.role), [user?.role]);
  const hasRole = (...roles: AppRole[]) => {
    if (!role) {
      return false;
    }

    return roles.includes(role);
  };
  const canAccessRoute =
    isAuthenticated && !!role && allowedRoles.includes(role);

  useEffect(() => {
    if (!isClientReady) {
      return;
    }

    if (!isAuthenticated && pathname !== loginPath) {
      router.replace(loginPath);
      return;
    }

    if (
      isAuthenticated &&
      (!role || !allowedRoles.includes(role)) &&
      pathname !== forbiddenPath
    ) {
      router.replace(forbiddenPath);
    }
  }, [
    allowedRoles,
    forbiddenPath,
    isAuthenticated,
    isClientReady,
    loginPath,
    pathname,
    role,
    router,
  ]);

  if (!isClientReady) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="text-muted-foreground text-sm">Carregando...</p>
      </div>
    );
  }

  if (!canAccessRoute) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="text-muted-foreground text-sm">Redirecionando...</p>
      </div>
    );
  }

  const guardProps: GuardChildProps = {
    isAuthenticated,
    user,
    role,
    roles: APP_ROLES,
    hasRole,
  };

  return (
    <RouteGuardContext.Provider value={guardProps}>
      {typeof children === "function" ? children(guardProps) : children}
    </RouteGuardContext.Provider>
  );
}
