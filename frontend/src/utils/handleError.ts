import { isApiError } from '../services/httpClient'
import type { ApiError } from '../services/httpClient'

const DEFAULT_MESSAGES: Record<number, string> = {
  400: '請求資料不正確',
  401: '尚未登入',
  403: '沒有操作權限',
  404: '找不到指定資料',
  409: '資料狀態衝突',
  500: '伺服器發生錯誤',
}

/** 驗證失敗時，後端會在 errors 帶回各欄位的具體訊息，優先顯示它們而不是籠統的 detail。 */
function getMessage(error: ApiError): string {
  const violations = error.problemDetails?.errors

  if (violations && violations.length > 0) {
    return violations.map((violation) => violation.message).join('\n')
  }

  return (
    error.problemDetails?.detail ??
    error.problemDetails?.title ??
    error.message ??
    DEFAULT_MESSAGES[error.status] ??
    '系統發生未知錯誤'
  )
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

  alert(getMessage(error))
}
