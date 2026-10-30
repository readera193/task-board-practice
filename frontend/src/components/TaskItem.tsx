import { useState } from 'react'
import type { FormEvent } from 'react'

import type { Task } from '../api/taskApi'

interface TaskItemProps {
  disabled: boolean
  task: Task
  onToggle: (id: number) => void
  onDelete: (id: number) => void
  onUpdate: (id: number, title: string, description: string) => Promise<boolean>
}

function TaskItem({ task, onToggle, onDelete, onUpdate, disabled }: TaskItemProps) {
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

    if (disabled || !title.trim()) {
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
            aria-label="編輯任務標題"
            maxLength={100}
            disabled={disabled}
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            placeholder="標題"
          />

          <textarea
            aria-label="編輯任務描述"
            maxLength={1000}
            disabled={disabled}
            className="task-description-input"
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            placeholder="描述（選填）"
          />

          <div className="task-edit-actions">
            <button type="submit" className="btn btn-primary" disabled={disabled}>
              儲存
            </button>

            <button
              type="button"
              className="btn btn-ghost"
              onClick={() => setEditing(false)}
              disabled={disabled}
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
        aria-label={`完成 ${task.title}`}
        disabled={disabled}
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

      <button type="button" className="btn btn-ghost" onClick={startEdit} disabled={disabled}>
        編輯
      </button>

      <button
        type="button"
        className="btn btn-danger"
        onClick={() => onDelete(task.id)}
        disabled={disabled}
      >
        刪除
      </button>
    </li>
  )
}

export default TaskItem
