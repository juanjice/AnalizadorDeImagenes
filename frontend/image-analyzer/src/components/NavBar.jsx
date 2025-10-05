import { Link, NavLink } from 'react-router-dom'

export default function NavBar() {
return (
    <header className="navbar">
        <div className="nav-inner">
        <Link to="/" className="brand">Inicio</Link>
            <nav className="nav-links">
                <NavLink to="/" end>Subir</NavLink>
            </nav>
        </div>
    </header>
)
}