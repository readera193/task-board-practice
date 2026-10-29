import { useCallback, useEffect, useState } from 'react'

import {
  createTask,
  deleteTask,
  getTasks,
  toggleTask,
  updateTask,
} from '../api/taskApi'
import type { Task } from '../api/taskApi'
import { handleError } from '../utils/handleError'

/** 任務清單與其操作；每個操作成功後重新載入清單，失敗時以 handleError 提示並回傳 false。 */
export function useTasks() {
  const [tasks, setTasks] = useState<Task[]>([])

  const reload = useCallback(async () => {
    try {
      const result = await getTasks()
      setTasks(result.items)
    } catch (error) {
      handleError(error)
    }
  }, [])

  // 掛載時載入第一次；以 cancelled 避免元件卸載（例如登出）後仍寫入 state
  useEffect(() => {
    let cancelled = false

    getTasks()
      .then((result) => {
        if (!cancelled) {
          setTasks(result.items)
        }
      })
      .catch(handleError)

    return () => {
      cancelled = true
    }
  }, [])

  const run = useCallback(
    async (action: () => Promise<unknown>) => {
      try {
        await action()
        await reload()
        return true
      } catch (error) {
        handleError(error)
        return false
      }
    },
    [reload],
  )

  const create = (title: string, description: string) =>
    run(() => createTask({ title, description: description || undefined }))

  const update = (id: number, title: string, description: string) =>
    run(() =>
      updateTask(id, { title, description: description || undefined }),
    )

  const toggle = (id: number) => run(() => toggleTask(id))

  const remove = (id: number) => run(() => deleteTask(id))

  return { tasks, create, update, toggle, remove }
}
