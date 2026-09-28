import httpClient from '../services/httpClient'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  username: string
  accessToken: string
  tokenType: string
}

export interface CurrentUser {
  username: string
}

export interface RegisterRequest {
  username: string
  password: string
}

export interface RegisterResponse {
  id: number
  username: string
}

export function login(
  request: LoginRequest,
): Promise<LoginResponse> {
  return httpClient.post<LoginResponse>(
    '/api/auth/login',
    request,
  )
}

export function register(
  request: RegisterRequest,
): Promise<RegisterResponse> {
  return httpClient.post<RegisterResponse>(
    '/api/users/register',
    request,
  )
}

export function getCurrentUser():
  Promise<CurrentUser> {
  return httpClient.get<CurrentUser>(
    '/api/auth/me',
  )
}