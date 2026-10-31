import { useCallback, useEffect, useState } from 'react';

import {
  demoLogin as demoLoginRequest,
  getCurrentUser,
  login as loginRequest,
  register as registerRequest,
} from '../api/authApi';
import {
  clearAccessToken,
  getAccessToken,
  getAuthChangedEventName,
  setAccessToken,
} from '../services/authStorage';
import { handleError } from '../utils/handleError';

export function useAuth() {
  const [currentUser, setCurrentUser] = useState<string | null>(null);
  // 有 token 才需要先向後端確認身分；沒有 token 時直接進入登入畫面
  const [initializing, setInitializing] = useState(() => getAccessToken() !== null);

  // httpClient 收到 401 會清掉 token 並發出事件，這裡同步回到未登入狀態
  useEffect(() => {
    function handleAuthChanged() {
      if (!getAccessToken()) {
        setCurrentUser(null);
      }
    }

    window.addEventListener(getAuthChangedEventName(), handleAuthChanged);

    return () => {
      window.removeEventListener(getAuthChangedEventName(), handleAuthChanged);
    };
  }, []);

  useEffect(() => {
    if (!getAccessToken()) {
      return;
    }

    let cancelled = false;

    getCurrentUser()
      .then((user) => {
        if (!cancelled) {
          setCurrentUser(user.username);
        }
      })
      .catch((error) => {
        clearAccessToken();
        handleError(error);
      })
      .finally(() => {
        if (!cancelled) {
          setInitializing(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, []);

  const login = useCallback(async (username: string, password: string) => {
    const result = await loginRequest({ username, password });

    setAccessToken(result.accessToken);
    setCurrentUser(result.username);
  }, []);

  const demoLogin = useCallback(async () => {
    const result = await demoLoginRequest();

    setAccessToken(result.accessToken);
    setCurrentUser(result.username);
  }, []);

  const register = useCallback(async (username: string, password: string) => {
    const result = await registerRequest({ username, password });

    return result.username;
  }, []);

  const logout = useCallback(() => {
    clearAccessToken();
    setCurrentUser(null);
  }, []);

  return { currentUser, initializing, login, demoLogin, register, logout };
}
