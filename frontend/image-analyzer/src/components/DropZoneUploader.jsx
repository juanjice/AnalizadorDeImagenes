import React, { useCallback, useMemo, useRef, useState } from 'react'
import { postImage } from '../api'

export default function DropzoneUploader() {
  const [file, setFile] = useState(null)
  const [previewUrl, setPreviewUrl] = useState(null)
  const [dragActive, setDragActive] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState(null)
  const inputRef = useRef(null)

  const MAX_BYTES = 10 * 1024 * 1024 // 10 MB

  /** Validación del archivo */
  const validateAndSet = useCallback((f) => {
    setError('')
    setResult(null)
    if (!f) return

    if (!f.type?.startsWith('image/')) {
      setFile(null)
      setPreviewUrl(null)
      setError('Solo se permiten archivos de imagen (jpg, png, webp, etc.)')
      return
    }

    if (f.size > MAX_BYTES) {
      setFile(null)
      setPreviewUrl(null)
      setError('La imagen supera los 10 MB permitidos')
      return
    }

    setFile(f)
    setPreviewUrl(URL.createObjectURL(f))
  }, [])

  /** Manejadores de eventos Drag & Drop */
  const onDrop = useCallback((e) => {
    e.preventDefault()
    e.stopPropagation()
    setDragActive(false)
    const f = e.dataTransfer.files?.[0]
    validateAndSet(f)
  }, [validateAndSet])

  const onDragOver = useCallback((e) => {
    e.preventDefault()
    e.stopPropagation()
    if (!dragActive) setDragActive(true)
  }, [dragActive])

  const onDragLeave = useCallback((e) => {
    e.preventDefault()
    e.stopPropagation()
    setDragActive(false)
  }, [])

  const onFileChange = useCallback((e) => {
    const f = e.target.files?.[0]
    validateAndSet(f)
  }, [validateAndSet])

  const onClickSelect = () => inputRef.current?.click()

  const onReset = () => {
    setFile(null)
    setPreviewUrl(null)
    setError('')
    setResult(null)
    if (inputRef.current) inputRef.current.value = ''
  }

  /** Envío al backend */
  const canSubmit = useMemo(() => !!file && !loading, [file, loading])

  const onSubmit = async () => {
    if (!file) return
    try {
      setLoading(true)
      setError('')
      const data = await postImage(file)
      setResult(data)
    } catch (err) {
      setError(err?.message || 'Error desconocido')
      setResult(null)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="uploader">
      <div
        className={`dropzone ${dragActive ? 'dragging' : ''}`}
        onDragOver={onDragOver}
        onDragLeave={onDragLeave}
        onDrop={onDrop}
        onClick={onClickSelect}
        role="button"
        tabIndex={0}
      >
        <input
          ref={inputRef}
          type="file"
          accept="image/*"
          onChange={onFileChange}
          className="file-input"
        />

        {!previewUrl && (
          <div className="placeholder">
            <p>Arrastra tu imagen aquí o haz clic para seleccionar</p>
            <small>(Solo imágenes · máx 10 MB)</small>
          </div>
        )}

        {previewUrl && (
          <div className="preview">
            <img src={previewUrl} alt="preview" />
          </div>
        )}
      </div>

      <div className="actions">
        <button className="btn" onClick={onClickSelect} type="button">
          Elegir imagen
        </button>

        <button
          className="btn primary"
          onClick={onSubmit}
          disabled={!canSubmit}
          type="button"
        >
          {loading ? 'Enviando…' : 'Analizar imagen'}
        </button>

        <button className="btn ghost" onClick={onReset} type="button">
          Limpiar
        </button>
      </div>

      {error && <div className="alert error">{error}</div>}

      {result && (
        <div className="panel">
          <h2>Su imagen esta siendo procesada</h2>
          <p>Id :{result.id}</p>            
          <p>Llave Imagen: {result.imageKey}</p>
          <p>Estado: {result.status}</p>    
          {console.log(result)}                 
        </div>
      )}
    </div>
  )
}