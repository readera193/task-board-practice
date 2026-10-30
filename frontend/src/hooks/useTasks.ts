import { useCallback, useEffect, useRef, useState } from 'react'

import {
  createTask,
  deleteTask,
  getTasks,
  toggleTask,
  updateTask,
} from '../api/taskApi'
import type { PagedResponse, Task } from '../api/taskApi'
import { handleError } from '../utils/handleError'

/** 分頁清單；寫入成功後重新讀取，讀取失敗可重試而不必再次寫入。 */
export function useTasks() {
  const [result, setResult] = useState<PagedResponse<Task> | null>(null)
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(true)
  const [loadFailed, setLoadFailed] = useState(false)
  const [busy, setBusy] = useState(false)
  const [refresh, setRefresh] = useState(0)
  const working = useRef(false)

  const reload = useCallback(() => {
    setLoading(true)
    setLoadFailed(false)
    setRefresh((value) => value + 1)
  }, [])

  const changePage = useCallback((next: number) => {
    setLoading(true)
    setLoadFailed(false)
    setPage(next)
  }, [])

  // 翻頁或卸載後忽略舊回應，避免慢速請求覆蓋目前頁面。
  useEffect(() => {
    let cancelled = false
    getTasks(page).then((next) => {
      if (cancelled) return
      const lastPage = Math.max(0, next.totalPages - 1)
      if (page > lastPage) {
        setPage(lastPage)
        return
      }
      setResult(next)
      setLoading(false)
    }).catch(() => {
      if (!cancelled) {
        setLoadFailed(true)
        setLoading(false)
      }
    })
    return () => {
      cancelled = true
    }
  }, [page, refresh])

  const run = useCallback(
    async (action: () => Promise<unknown>, firstPage = false) => {
      if (working.current) return false
      working.current = true
      setBusy(true)
      try {
        await action()
        if (firstPage && page !== 0) changePage(0)
        else reload()
        // 寫入成功但清單更新失敗時，由清單提示重試，避免重複新增。
        return true
      } catch (error) {
        handleError(error)
        return false
      } finally {
        working.current = false
        setBusy(false)
      }
    },
    [reload, page, changePage],
  )

  const create = (title: string, description: string) =>
    run(() => createTask({ title, description: description || undefined }), true)

  const update = (id: number, title: string, description: string) =>
    run(() =>
      updateTask(id, { title, description: description || undefined }),
    )

  const toggle = (id: number) => run(() => toggleTask(id))

  const remove = (id: number) => run(() => deleteTask(id))

  return {
    tasks: result?.items ?? [], totalPages: result?.totalPages ?? 0,
    totalElements: result?.totalElements ?? 0, page, setPage: changePage,
    loading, loadFailed, busy, reload, create, update, toggle, remove,
  }
}
