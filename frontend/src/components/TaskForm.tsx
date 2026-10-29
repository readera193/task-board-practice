import { useState } from 'react'
import type { FormEvent } from 'react'

interface TaskFormProps {
  onCreate: (title: string, description: string) => Promise<boolean>
}

function TaskForm({ onCreate }: TaskFormProps) {
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!title.trim()) {
      return
    }

    if (await onCreate(title, description)) {
      setTitle('')
      setDescription('')
    }
  }

  return (
    <form className="task-form" onSubmit={handleSubmit}>
      <input
        value={title}
        onChange={(event) => setTitle(event.target.value)}
        placeholder="新增任務"
      />

      <textarea
        className="task-description-input"
        value={description}
        onChange={(event) => setDescription(event.target.value)}
        placeholder="描述（選填）"
      />

      <button type="submit" className="btn btn-primary">
        新增
      </button>
    </form>
  )
}

export default TaskForm
