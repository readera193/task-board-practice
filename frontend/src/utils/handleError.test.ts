import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import { handleError } from './handleError'

describe('handleError', () => {
  const alertMock = vi.fn()

  beforeEach(() => {
    vi.stubGlobal('alert', alertMock)
    vi.spyOn(console, 'error').mockImplementation(() => {})
  })

  afterEach(() => {
    alertMock.mockReset()
    vi.unstubAllGlobals()
    vi.restoreAllMocks()
  })

  it('驗證失敗時顯示各欄位的具體訊息', () => {
    handleError({
      status: 400,
      problemDetails: {
        detail: '請求資料驗證失敗',
        code: 'VALIDATION_ERROR',
        errors: [
          { field: 'password', message: '密碼長度必須為 8 到 100 個字' },
          { field: 'username', message: '帳號不可為空' },
        ],
      },
    })

    expect(alertMock).toHaveBeenCalledWith(
      '密碼長度必須為 8 到 100 個字\n帳號不可為空',
    )
  })

  it('沒有欄位錯誤時顯示 detail', () => {
    handleError({
      status: 409,
      problemDetails: { detail: '帳號已存在', code: 'USERNAME_ALREADY_EXISTS' },
    })

    expect(alertMock).toHaveBeenCalledWith('帳號已存在')
  })

  it('沒有 problemDetails 時使用狀態碼對應的預設訊息', () => {
    handleError({ status: 403 })

    expect(alertMock).toHaveBeenCalledWith('沒有操作權限')
  })

  it('使用者主動取消的請求不提示', () => {
    handleError({ status: 0, isAbort: true, isTimeout: false })

    expect(alertMock).not.toHaveBeenCalled()
  })

  it('非 ApiError 時顯示未知錯誤', () => {
    handleError(new Error('boom'))

    expect(alertMock).toHaveBeenCalledWith('系統發生未知錯誤')
  })
})
