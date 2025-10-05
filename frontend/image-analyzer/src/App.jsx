import { useState } from 'react'
import './App.css'
import NavBar from './components/NavBar'
import DropzoneUploader from './components/DropZoneUploader'
import { Routes, Route } from 'react-router-dom'
function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <>
      <NavBar />
      <main className="container">
        <Routes>
          <Route path="/" element={<DropzoneUploader />} />
        </Routes>
      </main>
    </>
    </>
  )
}

export default App
