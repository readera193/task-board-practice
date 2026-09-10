import { isApiError } from '../services/httpClient'

const DEFAULT_MESSAGES: Record<number, string> = {
  400: '請求資料不正確',
  401: '尚未登入',
  403: '沒有操作權限',
  404: '找不到指定資料',
  409: '資料狀態衝突',
  500: '伺服器發生錯誤',
}

export function handleError(error: unknown): void {
  if (!isApiError(error)) {
    console.error(error)
    alert('系統發生未知錯誤')
    return
  }

  if (error.isAbort && !error.isTimeout) {
    return
  }

  console.error('API Error:', error)

  const message =
    error.problemDetails?.detail ??
    error.problemDetails?.title ??
    error.message ??
    DEFAULT_MESSAGES[error.status] ??
    '系統發生未知錯誤'

  alert(message)
}