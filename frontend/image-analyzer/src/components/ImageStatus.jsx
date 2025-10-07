import { useEffect, useMemo, useState } from 'react'
import { useParams } from 'react-router-dom'
import { getImageById } from '../api'

export default function ImageStatus() {
  const { id } = useParams()
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  const shouldPoll = useMemo(() => {
    return data && (data.status === 'PENDING' || data.status === 'PROCESSING')
  }, [data])

  useEffect(() => {
    let timer
    let cancelled = false
    const load = async () => {
      try {
        setLoading(true)
        const res = await getImageById(id)
        if (!cancelled) setData(res)
      } catch (e) {
        if (!cancelled) setError(e?.message || 'Error cargando estado')
      } finally {
        if (!cancelled) setLoading(false)
      }
    }
    load()
    if (shouldPoll) {
      timer = setInterval(load, 2000)
    }
    return () => {
      cancelled = true
      if (timer) clearInterval(timer)
    }
  }, [id, shouldPoll])

  if (loading && !data) return <div className="panel">Cargando…</div>
  if (error) return <div className="alert error">{error}</div>
  if (!data) return null

  return (
    <div className="panel">
      <h2>Estado de análisis</h2>
      <p>ID: {data.id}</p>
      <p>Estado: <strong>{data.status}</strong></p>
      {data.tags?.length > 0 && (
        <div>
          <h3>Tags</h3>
          <ul>
            {data.tags.map((t, i) => (
              <li key={i}>{t.label} — {(t.confidence*100).toFixed(1)}%</li>
            ))}
          </ul>
        </div>
      )}
      {data.presignedUrl && (
        <div style={{marginBlock: '1rem'}}>
          <img src={data.presignedUrl} alt="preview" style={{maxWidth: '100%', height: 'auto'}} />
        </div>
      )}
      
    </div>
  )
}

