import React from 'react';
import { Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import logo from "../../assets/image.png";

function Navbar() {
  const navLinkStyle = {
    transition: 'color 0.3s ease, transform 0.2s ease',
  };

  return (
    <>
      <style>
        {`
        .navbar-brand {
          color: #1b1b1b !important;
          font-weight: 700;
        }

        .navbar-brand:hover {
          color: #000 !important;
        }

        .nav-link {
          color: #5f4444 !important;
          font-weight: 500;
        }

        .custom-nav-link:hover {
          color: #2a4060 !important;
          transform: translateY(-2px);
        }

        .custom-nav-link {
          transition: all 0.3s ease;
        }

        @media (max-width: 991px) {
          .navbar-nav {
            text-align: center;
          }
        }
        `}
      </style>

      <nav
        className="navbar navbar-expand-lg navbar-light py-2"
        style={{
          background: "#fff",
          position: "fixed",
          top: 0,
          width: "100%",
          zIndex: 1000,
          boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
        }}
      >
        <div className="container-fluid">
          {/* Logo */}
          <Link className="navbar-brand d-flex align-items-center" to="/">
            <img
              src={logo}
              alt="Logo"
              className="me-2"
              style={{ height: "55px", width: "auto" }}
            />
          </Link>

          {/* Hamburger Toggler for Mobile */}
          <button
            className="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarNav"
            aria-controls="navbarNav"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon"></span>
          </button>

          {/* Collapsible Navigation Menu */}
          <div className="collapse navbar-collapse" id="navbarNav">
            <ul className="navbar-nav mx-auto">
              {/* Flights */}
              <li className="nav-item">
                <Link
                  className="nav-link custom-nav-link d-flex flex-column align-items-center"
                  to="/flights"
                  style={navLinkStyle}
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    fill="currentColor"
                    className="bi bi-airplane-fill mb-1"
                    viewBox="0 0 16 16"
                  >
                    <path d="M6.428 1.151C6.708.591 7.213 0 8 0s1.292.592 1.572 1.151C9.861 1.73 10 2.431 10 3v3.691l5.17 2.585a1.5 1.5 0 0 1 .83 1.342V12a.5.5 0 0 1-.582.493l-5.507-.918-.375 2.253 1.318 1.318A.5.5 0 0 1 10.5 16h-5a.5.5 0 0 1-.354-.854l1.319-1.318-.376-2.253-5.507.918A.5.5 0 0 1 0 12v-1.382a1.5 1.5 0 0 1 .83-1.342L6 6.691V3c0-.568.14-1.271.428-1.849" />
                  </svg>
                  <span>Flights</span>
                </Link>
              </li>

              {/* Hotels */}
              <li className="nav-item">
                <a
                  className="nav-link custom-nav-link d-flex flex-column align-items-center"
                  href="#"
                  style={navLinkStyle}
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    fill="currentColor"
                    className="bi bi-buildings mb-1"
                    viewBox="0 0 16 16"
                  >
                    <path d="M14.763.075A.5.5 0 0 1 15 .5v15a.5.5 0 0 1-.5.5h-3a.5.5 0 0 1-.5-.5V14h-1v1.5a.5.5 0 0 1-.5.5h-9a.5.5 0 0 1-.5-.5V10a.5.5 0 0 1 .342-.474L6 7.64V4.5a.5.5 0 0 1 .276-.447l8-4a.5.5 0 0 1 .487.022M6 8.694 1 10.36V15h5zM7 15h2v-1.5a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 .5.5V15h2V1.309l-7 3.5z" />
                    <path d="M2 11h1v1H2zm2 0h1v1H4zm-2 2h1v1H2zm2 0h1v1H4zm4-4h1v1H8zm2 0h1v1h-1zm-2 2h1v1H8zm2 0h1v1h-1zm2-2h1v1h-1zm0 2h1v1h-1zM8 7h1v1H8zm2 0h1v1h-1zm2 0h1v1h-1zM8 5h1v1H8zm2 0h1v1h-1zm2 0h1v1h-1zm0-2h1v1h-1z" />
                  </svg>
                  <span>Hotels</span>
                </a>
              </li>

              {/* Packages */}
              <li className="nav-item mx-2">
                <a
                  className="nav-link custom-nav-link d-flex flex-column align-items-center"
                  href="#"
                  style={navLinkStyle}
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    fill="currentColor"
                    className="bi bi-box mb-1"
                    viewBox="0 0 16 16"
                  >
                    <path d="M8.186 1.113a.5.5 0 0 0-.372 0L1.846 3.5 8 5.961 14.154 3.5zM15 4.239l-6.5 2.6v7.922l6.5-2.6V4.24zM7.5 14.762V6.838L1 4.239v7.923zM7.443.184a1.5 1.5 0 0 1 1.114 0l7.129 2.852A.5.5 0 0 1 16 3.5v8.662a1 1 0 0 1-.629.928l-7.185 2.874a.5.5 0 0 1-.372 0L.63 13.09a1 1 0 0 1-.63-.928V3.5a.5.5 0 0 1 .314-.464z" />
                  </svg>
                  <span>Packages</span>
                </a>
              </li>

              {/* Trains */}
              <li className="nav-item mx-2">
                <a
                  className="nav-link custom-nav-link d-flex flex-column align-items-center"
                  href="#"
                  style={navLinkStyle}
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className="mb-1"
                  >
                    <rect x="7" y="5" width="10" height="14" rx="1"></rect>
                    <path d="M7 11h10"></path>
                    <path d="M9 19l-2 3"></path>
                    <path d="M15 19l2 3"></path>
                  </svg>
                  <span>Trains</span>
                </a>
              </li>

              {/* Buses */}
              <li className="nav-item mx-2">
                <a
                  className="nav-link custom-nav-link d-flex flex-column align-items-center"
                  href="#"
                  style={navLinkStyle}
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className="mb-1"
                  >
                    <path d="M8 6v6"></path>
                    <path d="M15 6v6"></path>
                    <path d="M2 12h20v5a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2v-5Z"></path>
                    <path d="M6 19v2"></path>
                    <path d="M18 19v2"></path>
                  </svg>
                  <span>Buses</span>
                </a>
              </li>

              {/* Cabs */}
              <li className="nav-item mx-2">
                <a
                  className="nav-link custom-nav-link d-flex flex-column align-items-center"
                  href="#"
                  style={navLinkStyle}
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className="mb-1"
                  >
                    <path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2"></path>
                    <circle cx="7" cy="17" r="2"></circle>
                    <path d="M9 17h6"></path>
                    <circle cx="17" cy="17" r="2"></circle>
                  </svg>
                  <span>Cabs</span>
                </a>
              </li>

              {/* Activities */}
              <li className="nav-item mx-2">
                <a
                  className="nav-link custom-nav-link d-flex flex-column align-items-center"
                  href="#"
                  style={navLinkStyle}
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className="mb-1"
                  >
                    <path d="m12 12 8-8"></path>
                    <path d="m20 12-8-8"></path>
                    <path d="m12 20 8-8"></path>
                    <path d="m20 20-8-8"></path>
                  </svg>
                  <span>Activities</span>
                </a>
              </li>
            </ul>

            {/* Right-aligned User Links */}
            <ul className="navbar-nav ms-auto">
              <li className="nav-item">
                <a className="nav-link custom-nav-link" href="#" style={navLinkStyle}>
                  My Trips
                </a>
              </li>
              <li className="nav-item">
                <a className="nav-link custom-nav-link" href="#" style={navLinkStyle}>
                  Budget
                </a>
              </li>
              <li className="nav-item">
                <a className="nav-link custom-nav-link" href="#" style={navLinkStyle}>
                  Recommendations
                </a>
              </li>
              <li className="nav-item">
                <a className="nav-link custom-nav-link" href="#" style={navLinkStyle}>
                  Alerts
                </a>
              </li>
              <li className="nav-item">
                <Link className="nav-link custom-nav-link" to="/register" style={navLinkStyle}>
                  Register
                </Link>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    </>
  );
}

export default Navbar;