import { useState } from 'react';
import type { FormEvent } from 'react';

interface TaskFormProps {
  disabled: boolean;
  onCreate: (title: string, description: string) => Promise<boolean>;
}

function TaskForm({ onCreate, disabled }: TaskFormProps) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (disabled || !title.trim()) {
      return;
    }

    if (await onCreate(title, description)) {
      setTitle('');
      setDescription('');
    }
  }

  return (
    <form className="task-form" onSubmit={handleSubmit}>
      <input
        aria-label="新增任務標題"
        maxLength={100}
        disabled={disabled}
        value={title}
        onChange={(event) => setTitle(event.target.value)}
        placeholder="新增任務"
      />

      <textarea
        aria-label="新增任務描述"
        maxLength={1000}
        disabled={disabled}
        className="task-description-input"
        value={description}
        onChange={(event) => setDescription(event.target.value)}
        placeholder="描述（選填）"
      />

      <button type="submit" className="btn btn-primary" disabled={disabled}>
        新增
      </button>
    </form>
  );
}

export default TaskForm;
