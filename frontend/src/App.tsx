import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'

import {
  getCurrentUser,
  login,
  register,
} from './api/authApi'

import {
  createTask,
  deleteTask,
  getTasks,
  toggleTask,
  updateTask,
} from './api/taskApi'
import type { Task } from './api/taskApi'

import {
  clearAccessToken,
  getAccessToken,
  getAuthChangedEventName,
  setAccessToken,
} from './services/authStorage'

import { handleError } from './utils/handleError'

import './App.css'

function App() {
  const [tasks, setTasks] = useState<Task[]>([])
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')

  const [authMode, setAuthMode] =
    useState<'login' | 'register'>('login')

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')

  const [registerUsername, setRegisterUsername] = useState('')
  const [registerPassword, setRegisterPassword] = useState('')

  const [editingId, setEditingId] = useState<number | null>(null)
  const [editTitle, setEditTitle] = useState('')
  const [editDescription, setEditDescription] = useState('')

  const [currentUser, setCurrentUser] =
    useState<string | null>(null)

  const [loading, setLoading] = useState(true)

  useEffect(() => {
    initialize()
  }, [])

  useEffect(() => {
    function handleAuthChanged() {
      if (!getAccessToken()) {
        setCurrentUser(null)
        setTasks([])
      }
    }

    window.addEventListener(
      getAuthChangedEventName(),
      handleAuthChanged,
    )

    return () => {
      window.removeEventListener(
        getAuthChangedEventName(),
        handleAuthChanged,
      )
    }
  }, [])

  async function initialize() {
    const token = getAccessToken()

    if (!token) {
      setLoading(false)
      return
    }

    try {
      const user = await getCurrentUser()

      setCurrentUser(user.username)

      await loadTasks()
    } catch (error) {
      clearAccessToken()
      setCurrentUser(null)

      handleError(error)
    } finally {
      setLoading(false)
    }
  }

  async function loadTasks() {
    try {
      const result = await getTasks()
      setTasks(result.items)
    } catch (error) {
      handleError(error)
    } finally {
      setLoading(false)
    }
  }

  async function handleLogin(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault()

    try {
      const result = await login({
        username,
        password,
      })

      setAccessToken(result.accessToken)
      setCurrentUser(result.username)

      setPassword('')

      await loadTasks()
    } catch (error) {
      handleError(error)
    }
  }

  async function handleRegister(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault()

    try {
      const result = await register({
        username: registerUsername,
        password: registerPassword,
      })

      setUsername(result.username)
      setRegisterUsername('')
      setRegisterPassword('')
      setAuthMode('login')

      alert('註冊成功，請登入')
    } catch (error) {
      handleError(error)
    }
  }

  function handleLogout() {
    clearAccessToken()

    setCurrentUser(null)
    setTasks([])
    setUsername('')
    setPassword('')
  }

  async function handleCreate(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault()

    if (!title.trim()) {
      return
    }

    try {
      await createTask({
        title: title,
        description: description || undefined,
      })

      setTitle('')
      setDescription('')

      await loadTasks()
    } catch (error) {
      handleError(error)
    }
  }

  async function handleToggle(id: number) {
    try {
      await toggleTask(id)
      await loadTasks()
    } catch (error) {
      handleError(error)
    }
  }

  async function handleDelete(id: number) {
    try {
      await deleteTask(id)
      await loadTasks()
    } catch (error) {
      handleError(error)
    }
  }

  function startEdit(task: Task) {
    setEditingId(task.id)
    setEditTitle(task.title)
    setEditDescription(task.description ?? '')
  }

  function cancelEdit() {
    setEditingId(null)
  }

  async function handleUpdate(
    event: FormEvent<HTMLFormElement>,
    id: number,
  ) {
    event.preventDefault()

    if (!editTitle.trim()) {
      return
    }

    try {
      await updateTask(id, {
        title: editTitle,
        description: editDescription || undefined,
      })

      setEditingId(null)

      await loadTasks()
    } catch (error) {
      handleError(error)
    }
  }

  if (loading) {
    return <div className="loading-screen">載入中...</div>
  }

  if (!currentUser) {
    if (authMode === 'register') {
      return (
        <main className="page center">
          <div className="auth-card">
            <h1 className="auth-title">建立帳號</h1>
            <p className="auth-subtitle">註冊帳號以開始管理你的任務</p>

            <form onSubmit={handleRegister}>
              <div className="field">
                <label htmlFor="register-username">帳號</label>
                <input
                  id="register-username"
                  value={registerUsername}
                  onChange={(event) =>
                    setRegisterUsername(event.target.value)
                  }
                />
              </div>

              <div className="field">
                <label htmlFor="register-password">密碼</label>
                <input
                  id="register-password"
                  type="password"
                  value={registerPassword}
                  onChange={(event) =>
                    setRegisterPassword(event.target.value)
                  }
                />
              </div>

              <button type="submit" className="btn btn-primary">
                註冊
              </button>
            </form>

            <button
              type="button"
              className="btn btn-link"
              onClick={() => setAuthMode('login')}
            >
              已經有帳號了？前往登入
            </button>
          </div>
        </main>
      )
    }

    return (
      <main className="page center">
        <div className="auth-card">
          <h1 className="auth-title">歡迎回來</h1>
          <p className="auth-subtitle">登入以管理你的任務</p>

          <form onSubmit={handleLogin}>
            <div className="field">
              <label htmlFor="username">帳號</label>
              <input
                id="username"
                value={username}
                onChange={(event) =>
                  setUsername(event.target.value)
                }
              />
            </div>

            <div className="field">
              <label htmlFor="password">密碼</label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(event) =>
                  setPassword(event.target.value)
                }
              />
            </div>

            <button type="submit" className="btn btn-primary">
              登入
            </button>
          </form>

          <button
            type="button"
            className="btn btn-link"
            onClick={() => setAuthMode('register')}
          >
            還沒有帳號？前往註冊
          </button>
        </div>
      </main>
    )
  }

  return (
    <main className="page center">
      <div className="board-card">
        <div className="board-header">
          <span className="user-chip">
            目前登入身分：<strong>{currentUser}</strong>
          </span>

          <button
            type="button"
            className="btn btn-ghost"
            onClick={handleLogout}
          >
            登出
          </button>
        </div>

        <h1 className="board-title">任務看板</h1>

        <form className="task-form" onSubmit={handleCreate}>
          <input
            value={title}
            onChange={(event) =>
              setTitle(event.target.value)
            }
            placeholder="新增任務"
          />

          <textarea
            className="task-description-input"
            value={description}
            onChange={(event) =>
              setDescription(event.target.value)
            }
            placeholder="描述（選填）"
          />

          <button type="submit" className="btn btn-primary">
            新增
          </button>
        </form>

        {tasks.length === 0 ? (
          <p className="empty-state">目前尚無任務，請在上方新增一筆。</p>
        ) : (
          <ul className="task-list">
            {tasks.map((task) => (
              <li key={task.id} className="task-item">
                {editingId === task.id ? (
                  <form
                    className="task-edit-form"
                    onSubmit={(event) => handleUpdate(event, task.id)}
                  >
                    <input
                      value={editTitle}
                      onChange={(event) =>
                        setEditTitle(event.target.value)
                      }
                      placeholder="標題"
                    />

                    <textarea
                      className="task-description-input"
                      value={editDescription}
                      onChange={(event) =>
                        setEditDescription(event.target.value)
                      }
                      placeholder="描述（選填）"
                    />

                    <div className="task-edit-actions">
                      <button type="submit" className="btn btn-primary">
                        儲存
                      </button>

                      <button
                        type="button"
                        className="btn btn-ghost"
                        onClick={cancelEdit}
                      >
                        取消
                      </button>
                    </div>
                  </form>
                ) : (
                  <>
                    <input
                      type="checkbox"
                      checked={task.completed}
                      onChange={() =>
                        handleToggle(task.id)
                      }
                    />

                    <div className="task-content">
                      <span
                        className={
                          task.completed
                            ? 'task-title completed'
                            : 'task-title'
                        }
                      >
                        {task.title}
                      </span>

                      {task.description && (
                        <p className="task-description">
                          {task.description}
                        </p>
                      )}
                    </div>

                    <button
                      type="button"
                      className="btn btn-ghost"
                      onClick={() => startEdit(task)}
                    >
                      編輯
                    </button>

                    <button
                      type="button"
                      className="btn btn-danger"
                      onClick={() =>
                        handleDelete(task.id)
                      }
                    >
                      刪除
                    </button>
                  </>
                )}
              </li>
            ))}
          </ul>
        )}
      </div>
    </main>
  )
}

export default App
