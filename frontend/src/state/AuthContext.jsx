import { createContext, useContext, useMemo, useState } from "react";
import { forumApi } from "../api/forumApi";

const AuthContext = createContext(null);

function decodeJwt(token) {
  try {
    const payload = token.split(".")[1];
    return JSON.parse(atob(payload));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem("forum_token"));

  const user = useMemo(() => {
    if (!token) return null;

    const data = decodeJwt(token);

    return {
      username: data?.sub ?? "Unknown user",
      role: data?.role ?? data?.authorities?.[0] ?? "USER"
    };
  }, [token]);

  async function login(username, password) {
    const result = await forumApi.login(username, password);

    localStorage.setItem("forum_token", result.accessToken);
    setToken(result.accessToken);
  }

  function logout() {
    localStorage.removeItem("forum_token");
    setToken(null);
  }

  return (
    <AuthContext.Provider value={{ token, user, login, logout, isLogged: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}