import 'bootstrap/dist/css/bootstrap.min.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Navbar from './components/common/Navbar.jsx'
import Home from './pages/home.jsx'
import FlightPage from './pages/flights.jsx'

function App() {
  return (
    <Router>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/flights" element={<FlightPage />} />
        <Route path="/hotels" element={<HotelsPage />} />
        <Route path="/packages" element={<PackagesPage />} />
        <Route path="/activities" element={<ActivitiesPage />} />
        <Route path="/register" element={<RegisterPage />} />
        
        {/* 404 Page - Catch all */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Router>
  )
}

// Placeholder pages (replace with actual components later)
function HotelsPage() {
  return (
    <div style={{ 
      paddingTop: '120px', 
      textAlign: 'center', 
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%)'
    }}>
      <div>
        <h1 style={{ fontSize: '3rem', color: '#1e3a8a', marginBottom: '1rem' }}>🏨 Hotels</h1>
        <p style={{ fontSize: '1.2rem', color: '#666' }}>Hotels Page Coming Soon</p>
      </div>
    </div>
  )
}

function PackagesPage() {
  return (
    <div style={{ 
      paddingTop: '120px', 
      textAlign: 'center', 
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #fef3c7 0%, #fde68a 100%)'
    }}>
      <div>
        <h1 style={{ fontSize: '3rem', color: '#1e3a8a', marginBottom: '1rem' }}>📦 Packages</h1>
        <p style={{ fontSize: '1.2rem', color: '#666' }}>Packages Page Coming Soon</p>
      </div>
    </div>
  )
}

function ActivitiesPage() {
  return (
    <div style={{ 
      paddingTop: '120px', 
      textAlign: 'center', 
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%)'
    }}>
      <div>
        <h1 style={{ fontSize: '3rem', color: '#1e3a8a', marginBottom: '1rem' }}>🎯 Activities</h1>
        <p style={{ fontSize: '1.2rem', color: '#666' }}>Activities Page Coming Soon</p>
      </div>
    </div>
  )
}

function RegisterPage() {
  return (
    <div style={{ 
      paddingTop: '120px', 
      textAlign: 'center', 
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #f3e8ff 0%, #ede9fe 100%)'
    }}>
      <div>
        <h1 style={{ fontSize: '3rem', color: '#1e3a8a', marginBottom: '1rem' }}>📝 Register</h1>
        <p style={{ fontSize: '1.2rem', color: '#666' }}>Register Page Coming Soon</p>
      </div>
    </div>
  )
}

function NotFoundPage() {
  return (
    <div style={{ 
      paddingTop: '120px', 
      textAlign: 'center', 
      minHeight: '100vh',
      paddingBottom: '100px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #fee2e2 0%, #fecaca 100%)',
      flexDirection: 'column'
    }}>
      <h1 style={{ fontSize: '5rem', color: '#dc2626', marginBottom: '1rem', fontWeight: 'bold' }}>404</h1>
      <h2 style={{ fontSize: '2rem', color: '#1e3a8a', marginBottom: '1rem' }}>Page Not Found</h2>
      <p style={{ fontSize: '1.2rem', color: '#666' }}>Sorry, the page you're looking for doesn't exist.</p>
    </div>
  )
}

export default App