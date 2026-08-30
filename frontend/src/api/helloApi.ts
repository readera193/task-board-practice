import httpClient from '../services/httpClient'

export function getHello(): Promise<string> {
  return httpClient.get<string>('/api/hello')
}