import httpClient from '../services/httpClient'

export interface PagedResponse<T> {
  items: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface Task {
  id: number
  title: string
  completed: boolean
  createdAt: string
  description: string | null
}

export interface CreateTaskRequest {
  title: string
  description?: string
}

export interface UpdateTaskRequest {
  title: string
  description?: string
}

export function getTasks(): Promise<PagedResponse<Task>> {
  return httpClient.get<PagedResponse<Task>>('/api/tasks')
}

export function createTask(
  request: CreateTaskRequest,
): Promise<Task> {
  return httpClient.post<Task>(
    '/api/tasks',
    request,
  )
}

export function updateTask(
  id: number,
  request: UpdateTaskRequest,
): Promise<Task> {
  return httpClient.put<Task>(
    `/api/tasks/${id}`,
    request,
  )
}

export function toggleTask(id: number): Promise<Task> {
  return httpClient.patch<Task>(
    `/api/tasks/${id}/toggle`,
  )
}

export function deleteTask(id: number): Promise<void> {
  return httpClient.delete<void>(
    `/api/tasks/${id}`,
  )
}