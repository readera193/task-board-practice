import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'

import {
  createTask,
  deleteTask,
  getTasks,
  toggleTask,
} from './api/taskApi'
import type { Task } from './api/taskApi'

import { handleError } from './utils/handleError'

function App() {
  const [tasks, setTasks] = useState<Task[]>([])
  const [title, setTitle] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadTasks()
  }, [])

  async function loadTasks() {
    try {
      const result = await getTasks()
      setTasks(result)
    } catch (error) {
      handleError(error)
    } finally {
      setLoading(false)
    }
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()

    try {
      const task = await createTask({
        title,
      })

      setTasks(current => [
        ...current,
        task,
      ])

      setTitle('')
    } catch (error) {
      handleError(error)
    }
  }

  async function handleToggle(id: number) {
    try {
      const updatedTask = await toggleTask(id)

      setTasks(current =>
        current.map(task =>
          task.id === id
            ? updatedTask
            : task,
        ),
      )
    } catch (error) {
      handleError(error)
    }
  }

  async function handleDelete(id: number) {
    try {
      await deleteTask(id)

      setTasks(current =>
        current.filter(task =>
          task.id !== id,
        ),
      )
    } catch (error) {
      handleError(error)
    }
  }

  return (
    <main>
      <h1>Task Board</h1>

      <form onSubmit={handleSubmit}>
        <input
          value={title}
          onChange={event =>
            setTitle(event.target.value)
          }
          placeholder="輸入待辦事項"
        />

        <button type="submit">
          新增
        </button>
      </form>

      {loading && <p>Loading...</p>}

      {!loading && tasks.length === 0 && (
        <p>目前沒有待辦事項</p>
      )}

      <ul>
        {tasks.map(task => (
          <li key={task.id}>
            <label>
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
            </label>

            <button
              type="button"
              onClick={() =>
                handleDelete(task.id)
              }
            >
              刪除
            </button>
          </li>
        ))}
      </ul>
    </main>
  )
}

export default App