import 'bootstrap/dist/css/bootstrap.min.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext.jsx'
import Navbar from './components/common/Navbar.jsx'
import Home from './pages/home.jsx'
import FlightPage from './pages/flights.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import LoginPage from './pages/LoginPage.jsx'

function App() {
  return (
    <AuthProvider>
      <Router>
        <Navbar />
        <Routes>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="*"
            element={
              <div className="container-fluid" style={{ paddingTop: '120px' }}>
                <Routes>
                  <Route path="/" element={<Home />} />
                  <Route path="/flights" element={<FlightPage />} />
                  <Route path="/hotels" element={<HotelsPage />} />
                  <Route path="/packages" element={<PackagesPage />} />
                  <Route path="/activities" element={<ActivitiesPage />} />
                  <Route path="*" element={<NotFoundPage />} />
                </Routes>
              </div>
            }
          />
        </Routes>
      </Router>
    </AuthProvider>
  )
}

function HotelsPage() {
  return (
    <div className="d-flex align-items-center justify-content-center vh-100" style={{ background: 'linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%)' }}>
      <div className="text-center">
        <h1 style={{ fontSize: '3rem', color: '#1e3a8a', marginBottom: '1rem' }}>🏨 Hotels</h1>
        <p style={{ fontSize: '1.2rem', color: '#666' }}>Hotels Page Coming Soon</p>
      </div>
    </div>
  )
}

function PackagesPage() {
  return (
    <div className="d-flex align-items-center justify-content-center vh-100" style={{ background: 'linear-gradient(135deg, #fef3c7 0%, #fde68a 100%)' }}>
      <div className="text-center">
        <h1 style={{ fontSize: '3rem', color: '#1e3a8a', marginBottom: '1rem' }}>📦 Packages</h1>
        <p style={{ fontSize: '1.2rem', color: '#666' }}>Packages Page Coming Soon</p>
      </div>
    </div>
  )
}

function ActivitiesPage() {
  return (
    <div className="d-flex align-items-center justify-content-center vh-100" style={{ background: 'linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%)' }}>
      <div className="text-center">
        <h1 style={{ fontSize: '3rem', color: '#1e3a8a', marginBottom: '1rem' }}>🎯 Activities</h1>
        <p style={{ fontSize: '1.2rem', color: '#666' }}>Activities Page Coming Soon</p>
      </div>
    </div>
  )
}

function NotFoundPage() {
  return (
    <div className="d-flex flex-column align-items-center justify-content-center vh-100" style={{ background: 'linear-gradient(135deg, #fee2e2 0%, #fecaca 100%)', paddingBottom: '100px' }}>
      <h1 style={{ fontSize: '5rem', color: '#dc2626', marginBottom: '1rem', fontWeight: 'bold' }}>404</h1>
      <h2 style={{ fontSize: '2rem', color: '#1e3a8a', marginBottom: '1rem' }}>Page Not Found</h2>
      <p style={{ fontSize: '1.2rem', color: '#666' }}>Sorry, the page you're looking for doesn't exist.</p>
    </div>
  )
}

export default App