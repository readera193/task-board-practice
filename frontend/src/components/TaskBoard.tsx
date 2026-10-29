import { useTasks } from '../hooks/useTasks'
import TaskForm from './TaskForm'
import TaskItem from './TaskItem'

interface TaskBoardProps {
  currentUser: string
  onLogout: () => void
}

function TaskBoard({ currentUser, onLogout }: TaskBoardProps) {
  const { tasks, create, update, toggle, remove } = useTasks()

  return (
    <main className="page center">
      <div className="board-card">
        <div className="board-header">
          <span className="user-chip">
            目前登入身分：<strong>{currentUser}</strong>
          </span>

          <button type="button" className="btn btn-ghost" onClick={onLogout}>
            登出
          </button>
        </div>

        <h1 className="board-title">任務看板</h1>

        <TaskForm onCreate={create} />

        {tasks.length === 0 ? (
          <p className="empty-state">目前尚無任務，請在上方新增一筆。</p>
        ) : (
          <ul className="task-list">
            {tasks.map((task) => (
              <TaskItem
                key={task.id}
                task={task}
                onToggle={toggle}
                onDelete={remove}
                onUpdate={update}
              />
            ))}
          </ul>
        )}
      </div>
    </main>
  )
}

export default TaskBoard
