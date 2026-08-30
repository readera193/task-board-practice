import { useState } from 'react'

function App() {
  const [count, setCount] = useState(0)

  return (
    <main>
      <h1>My Task Board</h1>

      <p>Frontend development environment is ready.</p>

      <button onClick={() => setCount(count + 1)}>
        Clicked {count} times
      </button>
    </main>
  )
}

export default App