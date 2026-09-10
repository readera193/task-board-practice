import httpClient from '../services/httpClient'

export interface Task {
  id: number
  title: string
  completed: boolean
  createdAt: string
}

export interface CreateTaskRequest {
  title: string
}

export function getTasks(): Promise<Task[]> {
  return httpClient.get<Task[]>('/api/tasks')
}

export function createTask(
  request: CreateTaskRequest,
): Promise<Task> {
  return httpClient.post<Task>(
    '/api/tasks',
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