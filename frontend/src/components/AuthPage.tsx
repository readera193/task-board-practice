import { useRef, useState } from 'react';
import type { FormEvent } from 'react';

import { handleError } from '../utils/handleError';

interface AuthPageProps {
  demoLoginEnabled: boolean;
  onLogin: (username: string, password: string) => Promise<void>;
  onDemoLogin: () => Promise<void>;
  onRegister: (username: string, password: string) => Promise<string>;
}

const MIN_PASSWORD_LENGTH = 8;

function AuthPage({ demoLoginEnabled, onLogin, onDemoLogin, onRegister }: AuthPageProps) {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const working = useRef(false);

  const isRegister = mode === 'register';

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (working.current) return;
    working.current = true;
    setSubmitting(true);

    try {
      if (isRegister) {
        await onRegister(username.trim(), password);
        setPassword('');
        setMode('login');
        alert('註冊成功，請登入');
      } else {
        await onLogin(username.trim(), password);
      }
    } catch (error) {
      handleError(error);
    } finally {
      working.current = false;
      setSubmitting(false);
    }
  }

  async function handleDemoLogin() {
    if (working.current) return;
    working.current = true;
    setSubmitting(true);

    try {
      await onDemoLogin();
    } catch (error) {
      handleError(error);
    } finally {
      working.current = false;
      setSubmitting(false);
    }
  }

  function switchMode() {
    setPassword('');
    setMode(isRegister ? 'login' : 'register');
  }

  return (
    <main className="page center">
      <div className="auth-card">
        <h1 className="auth-title">{isRegister ? '建立帳號' : '歡迎回來'}</h1>
        <p className="auth-subtitle">
          {isRegister ? '註冊帳號以開始管理你的任務' : '登入以管理你的任務'}
        </p>

        <form onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="username">帳號</label>
            <input
              id="username"
              required
              maxLength={50}
              disabled={submitting}
              autoComplete="username"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
            />
          </div>

          <div className="field">
            <label htmlFor="password">密碼</label>
            <input
              id="password"
              required
              minLength={isRegister ? MIN_PASSWORD_LENGTH : undefined}
              maxLength={100}
              disabled={submitting}
              type="password"
              autoComplete={isRegister ? 'new-password' : 'current-password'}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
            {isRegister && (
              <small className="field-hint">密碼長度至少 {MIN_PASSWORD_LENGTH} 個字元</small>
            )}
          </div>

          <button type="submit" className="btn btn-primary" disabled={submitting}>
            {isRegister ? '註冊' : '登入'}
          </button>
        </form>

        {demoLoginEnabled && (
          <button
            type="button"
            className="btn btn-ghost"
            onClick={handleDemoLogin}
            disabled={submitting}
          >
            Demo 體驗（免註冊）
          </button>
        )}

        <button type="button" className="btn btn-link" onClick={switchMode} disabled={submitting}>
          {isRegister ? '已經有帳號了？前往登入' : '還沒有帳號？前往註冊'}
        </button>
      </div>
    </main>
  );
}

export default AuthPage;
