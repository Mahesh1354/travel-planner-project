import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";

function Hero() {
  return (
    <section
      className="vw-100 min-vh-100 p-0 m-0 overflow-hidden"
      style={{
        backgroundImage:
          "url('https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&q=80')",
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundAttachment: "fixed", // Parallax effect for that premium feel
      }}
    >
      {/* Dark overlay with better contrast gradient */}
      <div 
        className="min-vh-100 w-100 d-flex align-items-center"
        style={{ background: "linear-gradient(to right, rgba(0,0,0,0.7), rgba(0,0,0,0.3))" }}
      >
        <div className="container">
          <div className="row">
            <div className="col-lg-7 col-xl-6 text-white">
              
              {/* Badge for extra "modern" points */}
              <span className="badge rounded-pill bg-warning text-dark mb-3 px-3 py-2 fw-bold text-uppercase">
                New: Summer 2026 Deals
              </span>

              <h1 className="display-2 fw-bold mb-3" style={{ lineHeight: "1.1" }}>
                Plan Your <span className="text-warning">Perfect Trip</span>
              </h1>

              <p className="lead fs-4 mb-4 opacity-90">
                Flights, hotels, packages, and experiences — all in one place.
                Travel smarter with <span className="fw-bold border-bottom border-warning">MyTrip</span>.
              </p>

              <div className="d-flex flex-wrap gap-3">
                <button className="btn btn-warning btn-lg px-5 py-3 fw-bold shadow-sm">
                  Explore Trips
                </button>
                <button className="btn btn-outline-light btn-lg px-5 py-3 fw-bold">
                  Create Plan
                </button>
              </div>

            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

export default Hero;