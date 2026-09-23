const ACCESS_TOKEN_KEY = 'accessToken'
const AUTH_CHANGED_EVENT = 'auth-changed'

export function getAccessToken(): string | null {
  return localStorage.getItem(ACCESS_TOKEN_KEY)
}

export function setAccessToken(
  accessToken: string,
): void {
  localStorage.setItem(
    ACCESS_TOKEN_KEY,
    accessToken,
  )
}

export function clearAccessToken(): void {
  localStorage.removeItem(ACCESS_TOKEN_KEY)

  window.dispatchEvent(
    new Event(AUTH_CHANGED_EVENT),
  )
}

export function getAuthChangedEventName(): string {
  return AUTH_CHANGED_EVENT
}