import { useState } from 'react'
import type { FormEvent } from 'react'

import { handleError } from '../utils/handleError'

interface AuthPageProps {
  onLogin: (username: string, password: string) => Promise<void>
  onRegister: (username: string, password: string) => Promise<string>
}

const MIN_PASSWORD_LENGTH = 8

function AuthPage({ onLogin, onRegister }: AuthPageProps) {
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')

  const isRegister = mode === 'register'

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      if (isRegister) {
        await onRegister(username, password)
        setPassword('')
        setMode('login')
        alert('註冊成功，請登入')
      } else {
        await onLogin(username, password)
      }
    } catch (error) {
      handleError(error)
    }
  }

  function switchMode() {
    setPassword('')
    setMode(isRegister ? 'login' : 'register')
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
              autoComplete="username"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
            />
          </div>

          <div className="field">
            <label htmlFor="password">密碼</label>
            <input
              id="password"
              type="password"
              autoComplete={isRegister ? 'new-password' : 'current-password'}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
            {isRegister && (
              <small className="field-hint">
                密碼長度至少 {MIN_PASSWORD_LENGTH} 個字元
              </small>
            )}
          </div>

          <button type="submit" className="btn btn-primary">
            {isRegister ? '註冊' : '登入'}
          </button>
        </form>

        <button type="button" className="btn btn-link" onClick={switchMode}>
          {isRegister ? '已經有帳號了？前往登入' : '還沒有帳號？前往註冊'}
        </button>
      </div>
    </main>
  )
}

export default AuthPage
