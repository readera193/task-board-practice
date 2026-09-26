import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'

import {
  getCurrentUser,
  login,
} from './api/authApi'

import {
  createTask,
  deleteTask,
  getTasks,
  toggleTask,
} from './api/taskApi'
import type { Task } from './api/taskApi'

import {
  clearAccessToken,
  getAccessToken,
  getAuthChangedEventName,
  setAccessToken,
} from './services/authStorage'

import { handleError } from './utils/handleError'

function App() {
  const [tasks, setTasks] = useState<Task[]>([])
  const [title, setTitle] = useState('')

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')

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
      });

      setTitle('')

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

  if (loading) {
    return <div>Loading...</div>
  }

  if (!currentUser) {
    return (
      <main>
        <h1>Login</h1>

        <form onSubmit={handleLogin}>
          <div>
            <label>
              Username
              <input
                value={username}
                onChange={(event) =>
                  setUsername(event.target.value)
                }
              />
            </label>
          </div>

          <div>
            <label>
              Password
              <input
                type="password"
                value={password}
                onChange={(event) =>
                  setPassword(event.target.value)
                }
              />
            </label>
          </div>

          <button type="submit">
            Login
          </button>
        </form>
      </main>
    )
  }

  return (
    <main>
      <div>
        <span>
          Login as: {currentUser}
        </span>

        <button
          type="button"
          onClick={handleLogout}
        >
          Logout
        </button>
      </div>

      <h1>Task Board</h1>

      <form onSubmit={handleCreate}>
        <input
          value={title}
          onChange={(event) =>
            setTitle(event.target.value)
          }
          placeholder="New task"
        />

        <button type="submit">
          Add
        </button>
      </form>

      <ul>
        {tasks.map((task) => (
          <li key={task.id}>
            <input
              type="checkbox"
              checked={task.completed}
              onChange={() =>
                handleToggle(task.id)
              }
            />

            <span>
              {task.title}
            </span>

            <button
              type="button"
              onClick={() =>
                handleDelete(task.id)
              }
            >
              Delete
            </button>
          </li>
        ))}
      </ul>
    </main>
  )
}

export default App