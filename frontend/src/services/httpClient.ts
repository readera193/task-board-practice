import {
  clearAccessToken,
  getAccessToken,
} from './authStorage'

export interface ProblemDetails {
  type?: string
  title?: string
  status?: number
  detail?: string
  instance?: string
  code?: string
}

export interface ApiError {
  status: number
  problemDetails?: ProblemDetails
  message?: string
  isAbort?: boolean
  isTimeout?: boolean
}

interface RequestOptions {
  headers?: Record<string, string>
  timeoutMs?: number
  signal?: AbortSignal
}

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ??
  (import.meta.env.PROD ? '' : 'http://localhost:8080')

async function parseResponse<T>(response: Response): Promise<T> {
  if (response.status === 204) {
    return null as T
  }

  const contentType = response.headers.get('content-type') ?? ''

  if (contentType.includes('json')) {
    return response.json() as Promise<T>
  }

  if (contentType.includes('text/')) {
    return response.text() as Promise<T>
  }

  return response.blob() as Promise<T>
}

async function httpRequest<T>(
  url: string,
  method: string,
  data?: unknown,
  options: RequestOptions = {},
): Promise<T> {
  const {
    headers = {},
    timeoutMs = 30000,
    signal: externalSignal,
  } = options

  const controller = new AbortController()
  let isTimeoutAbort = false

  const timeoutId = setTimeout(() => {
    isTimeoutAbort = true
    controller.abort()
  }, timeoutMs)

  const abortHandler = () => controller.abort()
  externalSignal?.addEventListener('abort', abortHandler)

  const hasBody = data !== undefined && data !== null
  const accessToken = getAccessToken()

  try {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      method,
      headers: {
        ...(hasBody
          ? { 'Content-Type': 'application/json' }
          : {}),
        ...(accessToken
          ? {
            Authorization: `Bearer ${accessToken}`,
          }
          : {}),
        ...headers,
      },
      body: hasBody ? JSON.stringify(data) : undefined,
      signal: controller.signal,
    })

    const responseData = await parseResponse<T>(response)

    if (!response.ok) {
      if (response.status === 401) {
        clearAccessToken()
      }
      throw {
        status: response.status,
        problemDetails:
          responseData && typeof responseData === 'object'
            ? (responseData as ProblemDetails)
            : undefined,
      } satisfies ApiError
    }

    return responseData
  } catch (error) {
    if ((error as { name?: string }).name === 'AbortError') {
      throw {
        status: 0,
        message: isTimeoutAbort ? '請求逾時' : '請求已取消',
        isAbort: true,
        isTimeout: isTimeoutAbort,
      } satisfies ApiError
    }

    if (isApiError(error)) {
      throw error
    }

    console.error(error)

    throw {
      status: 0,
      message: '無法連線至伺服器',
    } satisfies ApiError
  } finally {
    clearTimeout(timeoutId)
    externalSignal?.removeEventListener('abort', abortHandler)
  }
}

export function isApiError(error: unknown): error is ApiError {
  return (
    typeof error === 'object' &&
    error !== null &&
    'status' in error &&
    typeof (error as ApiError).status === 'number'
  )
}

const httpClient = {
  get<T>(url: string, options?: RequestOptions): Promise<T> {
    return httpRequest<T>(url, 'GET', undefined, options)
  },

  post<T>(
    url: string,
    data?: unknown,
    options?: RequestOptions,
  ): Promise<T> {
    return httpRequest<T>(url, 'POST', data, options)
  },

  put<T>(
    url: string,
    data?: unknown,
    options?: RequestOptions,
  ): Promise<T> {
    return httpRequest<T>(url, 'PUT', data, options)
  },

  patch<T>(
    url: string,
    data?: unknown,
    options?: RequestOptions,
  ): Promise<T> {
    return httpRequest<T>(url, 'PATCH', data, options)
  },

  delete<T>(url: string, options?: RequestOptions): Promise<T> {
    return httpRequest<T>(url, 'DELETE', undefined, options)
  },
}

export default httpClient
