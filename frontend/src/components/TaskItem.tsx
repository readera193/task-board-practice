import { useState } from 'react'
import type { FormEvent } from 'react'

import type { Task } from '../api/taskApi'

interface TaskItemProps {
  task: Task
  onToggle: (id: number) => void
  onDelete: (id: number) => void
  onUpdate: (id: number, title: string, description: string) => Promise<boolean>
}

function TaskItem({ task, onToggle, onDelete, onUpdate }: TaskItemProps) {
  const [editing, setEditing] = useState(false)
  const [title, setTitle] = useState(task.title)
  const [description, setDescription] = useState(task.description ?? '')

  function startEdit() {
    setTitle(task.title)
    setDescription(task.description ?? '')
    setEditing(true)
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!title.trim()) {
      return
    }

    if (await onUpdate(task.id, title, description)) {
      setEditing(false)
    }
  }

  if (editing) {
    return (
      <li className="task-item">
        <form className="task-edit-form" onSubmit={handleSubmit}>
          <input
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            placeholder="標題"
          />

          <textarea
            className="task-description-input"
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            placeholder="描述（選填）"
          />

          <div className="task-edit-actions">
            <button type="submit" className="btn btn-primary">
              儲存
            </button>

            <button
              type="button"
              className="btn btn-ghost"
              onClick={() => setEditing(false)}
            >
              取消
            </button>
          </div>
        </form>
      </li>
    )
  }

  return (
    <li className="task-item">
      <input
        type="checkbox"
        checked={task.completed}
        onChange={() => onToggle(task.id)}
      />

      <div className="task-content">
        <span className={task.completed ? 'task-title completed' : 'task-title'}>
          {task.title}
        </span>

        {task.description && (
          <p className="task-description">{task.description}</p>
        )}
      </div>

      <button type="button" className="btn btn-ghost" onClick={startEdit}>
        編輯
      </button>

      <button
        type="button"
        className="btn btn-danger"
        onClick={() => onDelete(task.id)}
      >
        刪除
      </button>
    </li>
  )
}

export default TaskItem
