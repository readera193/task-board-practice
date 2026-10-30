import { useTasks } from '../hooks/useTasks'
import TaskForm from './TaskForm'
import TaskItem from './TaskItem'

interface TaskBoardProps {
  currentUser: string
  onLogout: () => void
}

function TaskBoard({ currentUser, onLogout }: TaskBoardProps) {
  const { tasks, create, update, toggle, remove, page, setPage,
    totalPages, totalElements, loading, loadFailed, busy, reload } = useTasks()

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

        <TaskForm onCreate={create} disabled={busy || loading} />

        {loading ? (
          <p role="status">載入中...</p>
        ) : loadFailed ? (
          <div role="alert">
            <p>無法更新任務清單。若剛才已儲存成功，請重新載入清單，無需再次送出。</p>
            <button type="button" className="btn btn-ghost" onClick={() => void reload()}>重新載入</button>
          </div>
        ) : tasks.length === 0 ? (
          <p className="empty-state">目前尚無任務，請在上方新增一筆。</p>
        ) : (
          <ul className="task-list">
            {tasks.map((task) => (
              <TaskItem
                key={task.id}
                task={task}
                disabled={busy}
                onToggle={toggle}
                onDelete={remove}
                onUpdate={update}
              />
            ))}
          </ul>
        )}
        {!loading && !loadFailed && (
          <nav aria-label="任務分頁" className="board-header">
            <button type="button" className="btn btn-ghost" disabled={busy || page === 0} onClick={() => setPage(page - 1)}>上一頁</button>
            <span>第 {page + 1} / {Math.max(1, totalPages)} 頁（共 {totalElements} 筆）</span>
            <button type="button" className="btn btn-ghost" disabled={busy || page + 1 >= totalPages} onClick={() => setPage(page + 1)}>下一頁</button>
          </nav>
        )}
      </div>
    </main>
  )
}

export default TaskBoard
