import { useEffect, useState } from 'react'
import { getHello } from './api/helloApi'
import { handleError } from './utils/handleError'

function App() {
  const [message, setMessage] = useState('')

  useEffect(() => {
    getHello()
      .then(setMessage)
      .catch(handleError)
  }, [])

  return (
    <main>
      <h1>Task Board</h1>
      <p>{message}</p>
    </main>
  )
}

export default App