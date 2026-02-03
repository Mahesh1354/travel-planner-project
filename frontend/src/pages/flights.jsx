import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import flightBg from "../assets/flight2.jpg";

function FlightPage() {
  const [searchParams, setSearchParams] = useState({
    from: 'New Delhi',
    to: 'Bangalore',
    departure: '2026-01-31',
    return: '',
    tripType: 'oneway',
    passengers: '1',
    class: 'economy',
  });

  const [flights, setFlights] = useState([]);
  const [hasSearched, setHasSearched] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [filters, setFilters] = useState({
    priceRange: [0, 15000],
    stops: 'all',
    airlines: [],
  });
  const [selectedFlight, setSelectedFlight] = useState(null);

  // Enhanced mock flight data with real-time info
  const mockFlightData = [
    {
      id: 1,
      airline: 'Akasa Air',
      flightNumber: 'QP1350',
      departure: '20:50',
      arrival: '23:55',
      duration: '3h 05m',
      stops: 0,
      price: 6381,
      seats: 12,
      rating: 4.5,
      from: 'New Delhi',
      to: 'Bangalore',
      logo: '🔶',
      discount: '25% OFF',
      amenities: ['WiFi', 'Meals', 'Baggage'],
      liveStatus: 'On Time',
      statusColor: '#4caf50',
    },
    {
      id: 2,
      airline: 'IndiGo',
      flightNumber: 'IG204',
      departure: '09:30',
      arrival: '12:35',
      duration: '3h 05m',
      stops: 0,
      price: 6989,
      seats: 45,
      rating: 4.3,
      from: 'New Delhi',
      to: 'Bangalore',
      logo: '🔵',
      discount: '15% OFF',
      amenities: ['Meals', 'Baggage'],
      liveStatus: 'On Time',
      statusColor: '#4caf50',
    },
    {
      id: 3,
      airline: 'SpiceJet',
      flightNumber: 'SG512',
      departure: '14:20',
      arrival: '17:25',
      duration: '3h 05m',
      stops: 1,
      price: 6458,
      seats: 30,
      rating: 3.8,
      from: 'New Delhi',
      to: 'Bangalore',
      logo: '🔴',
      discount: '20% OFF',
      amenities: ['Baggage'],
      liveStatus: 'Delayed 15 min',
      statusColor: '#ff9800',
    },
    {
      id: 4,
      airline: 'Vistara',
      flightNumber: 'UK156',
      departure: '16:45',
      arrival: '19:50',
      duration: '3h 05m',
      stops: 0,
      price: 6779,
      seats: 8,
      rating: 4.7,
      from: 'New Delhi',
      to: 'Bangalore',
      logo: '✈️',
      discount: '18% OFF',
      amenities: ['WiFi', 'Meals', 'Baggage', 'Entertainment'],
      liveStatus: 'Boarding',
      statusColor: '#2196f3',
    },
    {
      id: 5,
      airline: 'Air India',
      flightNumber: 'AI611',
      departure: '11:15',
      arrival: '14:20',
      duration: '3h 05m',
      stops: 0,
      price: 7173,
      seats: 5,
      rating: 4.4,
      from: 'New Delhi',
      to: 'Bangalore',
      logo: '🛫',
      discount: '12% OFF',
      amenities: ['WiFi', 'Meals', 'Baggage', 'Entertainment'],
      liveStatus: 'On Time',
      statusColor: '#4caf50',
    },
  ];

  const handleSearch = (e) => {
    e.preventDefault();
    setIsLoading(true);
    setHasSearched(true);
    setTimeout(() => {
      setFlights(mockFlightData);
      setIsLoading(false);
    }, 800);
  };

  const filteredFlights = flights.filter(flight => {
    const inPrice = flight.price >= filters.priceRange[0] && flight.price <= filters.priceRange[1];
    const matchStops = filters.stops === 'all' || 
                       (filters.stops === 'nonstop' && flight.stops === 0) ||
                       (filters.stops === 'onestop' && flight.stops <= 1);
    return inPrice && matchStops;
  });

  const sortedFlights = [...filteredFlights].sort((a, b) => b.rating - a.rating);

  return (
    <>
      <style>{`
        * {
          margin: 0;
          padding: 0;
          box-sizing: border-box;
        }

        @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&family=Sora:wght@400;600;700&display=swap');

        body {
          font-family: 'Poppins', sans-serif;
        }

        .flight-container {
  width: 100vw;
  min-height: 100vh;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  padding-top: 120px;
}


        .flight-container::before {
          content: '';
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: 
            radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.08) 0%, transparent 50%),
            radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.08) 0%, transparent 50%);
          pointer-events: none;
          z-index: 0;
        }

        .flight-wrapper {
          position: relative;
          z-index: 1;
        }

        .search-section {
          background: white;
          border-radius: 20px;
          padding: 40px;
          margin-bottom: 40px;
          box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
          animation: slideDown 0.6s ease-out;
        }

        @keyframes slideDown {
          from {
            opacity: 0;
            transform: translateY(-30px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }

        .search-title {
          font-size: 2.2rem;
          font-weight: 700;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          margin-bottom: 30px;
          font-family: 'Sora', sans-serif;
        }

        .trip-type-buttons {
          display: flex;
          gap: 12px;
          margin-bottom: 30px;
          flex-wrap: wrap;
        }

        .trip-btn {
          padding: 10px 20px;
          border: 2px solid #e0e0e0;
          background: white;
          border-radius: 10px;
          cursor: pointer;
          font-weight: 600;
          transition: all 0.3s ease;
          color: #666;
        }

        .trip-btn.active {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          border-color: transparent;
          box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
        }

        .trip-btn:hover {
          border-color: #667eea;
          color: #667eea;
        }

        .form-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
          gap: 20px;
          margin-bottom: 30px;
        }

        .form-group {
          display: flex;
          flex-direction: column;
        }

        .form-label {
          font-size: 0.9rem;
          font-weight: 600;
          color: #333;
          margin-bottom: 10px;
          text-transform: uppercase;
          letter-spacing: 0.5px;
        }

        .form-input, .form-select {
          padding: 14px 16px;
          border: 2px solid #e0e0e0;
          border-radius: 10px;
          font-size: 1rem;
          font-family: 'Poppins', sans-serif;
          transition: all 0.3s ease;
          background: white;
        }

        .form-input:focus, .form-select:focus {
          outline: none;
          border-color: #667eea;
          box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }

        .search-button {
          padding: 14px 40px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          border: none;
          border-radius: 10px;
          font-size: 1.1rem;
          font-weight: 700;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
          width: 100%;
          align-self: flex-end;
        }

        .search-button:hover {
          transform: translateY(-3px);
          box-shadow: 0 15px 40px rgba(102, 126, 234, 0.4);
        }

        .results-wrapper {
          display: grid;
          grid-template-columns: 280px 1fr;
          gap: 30px;
          animation: fadeIn 0.6s ease-out;
        }

        @keyframes fadeIn {
          from {
            opacity: 0;
          }
          to {
            opacity: 1;
          }
        }

        .filters-sidebar {
          background: white;
          border-radius: 15px;
          padding: 25px;
          box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
          height: fit-content;
          position: sticky;
          top: 120px;
        }

        .filter-title {
          font-size: 1.3rem;
          font-weight: 700;
          color: #333;
          margin-bottom: 20px;
          padding-bottom: 15px;
          border-bottom: 3px solid #667eea;
        }

        .filter-group {
          margin-bottom: 25px;
        }

        .filter-label {
          font-size: 0.95rem;
          font-weight: 600;
          color: #555;
          margin-bottom: 12px;
          display: block;
        }

        .price-range-input {
          width: 100%;
          height: 6px;
          border-radius: 3px;
          background: #e0e0e0;
          outline: none;
          -webkit-appearance: none;
        }

        .price-range-input::-webkit-slider-thumb {
          -webkit-appearance: none;
          appearance: none;
          width: 18px;
          height: 18px;
          border-radius: 50%;
          background: #667eea;
          cursor: pointer;
          box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
        }

        .price-display {
          margin-top: 12px;
          font-weight: 600;
          color: #667eea;
          font-size: 1.1rem;
        }

        .radio-group {
          display: flex;
          flex-direction: column;
          gap: 10px;
        }

        .radio-label {
          display: flex;
          align-items: center;
          cursor: pointer;
          font-weight: 500;
          color: #555;
          transition: all 0.2s ease;
        }

        .radio-label:hover {
          color: #667eea;
        }

        .radio-label input {
          margin-right: 10px;
          cursor: pointer;
          accent-color: #667eea;
          width: 18px;
          height: 18px;
        }

        .flights-results {
          display: flex;
          flex-direction: column;
          gap: 16px;
        }

        .flight-card {
          background: white;
          border-radius: 15px;
          padding: 20px;
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          cursor: pointer;
          border: 2px solid transparent;
          overflow: hidden;
          position: relative;
        }

        .flight-card::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
          transition: left 0.5s ease;
        }

        .flight-card:hover {
          transform: translateY(-8px);
          box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
          border-color: #667eea;
        }

        .flight-card:hover::before {
          left: 100%;
        }

        .flight-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 20px;
        }

        .airline-info {
          display: flex;
          align-items: center;
          gap: 12px;
        }

        .airline-logo {
          font-size: 2.5rem;
          width: 50px;
          height: 50px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-radius: 12px;
        }

        .airline-details h3 {
          font-size: 1.1rem;
          font-weight: 700;
          color: #1a1a2e;
          margin: 0;
        }

        .airline-details p {
          font-size: 0.85rem;
          color: #999;
          margin: 4px 0 0 0;
        }

        .discount-badge {
          background: linear-gradient(135deg, #ff6b6b 0%, #ff8787 100%);
          color: white;
          padding: 8px 16px;
          border-radius: 20px;
          font-weight: 700;
          font-size: 0.9rem;
          box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
        }

        .status-badge {
          display: inline-block;
          padding: 6px 14px;
          border-radius: 20px;
          font-size: 0.85rem;
          font-weight: 600;
        }

        .flight-times {
          display: grid;
          grid-template-columns: 1fr auto 1fr;
          gap: 20px;
          align-items: center;
          margin-bottom: 20px;
          padding-bottom: 20px;
          border-bottom: 2px solid #f5f5f5;
        }

        .time-block {
          display: flex;
          flex-direction: column;
        }

        .time-value {
          font-size: 1.8rem;
          font-weight: 700;
          color: #1a1a2e;
          font-family: 'Sora', sans-serif;
        }

        .time-label {
          font-size: 0.85rem;
          color: #999;
          margin-top: 4px;
          font-weight: 500;
        }

        .flight-duration {
          text-align: center;
        }

        .duration-value {
          font-size: 0.95rem;
          color: #666;
          font-weight: 600;
          margin-bottom: 8px;
        }

        .duration-bar {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;
        }

        .bar {
          flex: 1;
          height: 3px;
          background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
          border-radius: 2px;
        }

        .stops-info {
          font-size: 0.8rem;
          color: #ff6b6b;
          font-weight: 600;
          margin-top: 4px;
        }

        .flight-amenities {
          display: flex;
          gap: 8px;
          margin-bottom: 16px;
          flex-wrap: wrap;
        }

        .amenity {
          background: #f5f5f5;
          padding: 6px 12px;
          border-radius: 20px;
          font-size: 0.8rem;
          color: #666;
          font-weight: 500;
        }

        .flight-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
        }

        .price-info {
          display: flex;
          flex-direction: column;
        }

        .price-label {
          font-size: 0.85rem;
          color: #999;
          margin-bottom: 4px;
        }

        .price-value {
          font-size: 1.8rem;
          font-weight: 700;
          color: #667eea;
          font-family: 'Sora', sans-serif;
        }

        .book-button {
          padding: 12px 28px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          border: none;
          border-radius: 10px;
          font-weight: 700;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
        }

        .book-button:hover {
          transform: scale(1.05);
          box-shadow: 0 12px 28px rgba(102, 126, 234, 0.4);
        }

        .loading-spinner {
          display: flex;
          justify-content: center;
          align-items: center;
          padding: 80px 20px;
        }

        .spinner {
          width: 50px;
          height: 50px;
          border: 4px solid rgba(255, 255, 255, 0.2);
          border-top-color: white;
          border-radius: 50%;
          animation: spin 1s linear infinite;
        }

        @keyframes spin {
          to { transform: rotate(360deg); }
        }

        .empty-state {
          text-align: center;
          padding: 60px 20px;
          background: white;
          border-radius: 15px;
          box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        }

        .empty-state-icon {
          font-size: 4rem;
          margin-bottom: 20px;
        }

        .empty-state-title {
          font-size: 1.5rem;
          font-weight: 700;
          color: #1a1a2e;
          margin-bottom: 10px;
        }

        .empty-state-text {
          color: #999;
          font-size: 1rem;
        }

        .results-header {
          color: white;
          margin-bottom: 20px;
          font-weight: 600;
          font-size: 1.1rem;
        }

        @media (max-width: 768px) {
          .results-wrapper {
            grid-template-columns: 1fr;
          }

          .filters-sidebar {
            position: static;
          }

          .form-grid {
            grid-template-columns: 1fr;
          }

          .search-section {
            padding: 25px;
          }

          .search-title {
            font-size: 1.8rem;
          }

          .flight-times {
            grid-template-columns: 1fr;
            gap: 12px;
          }
        }
      `}</style>

      <div
      className="flight-container"
      style={{ backgroundImage: `url(${flightBg})` }}
    >
        <div className="flight-wrapper" style={{ maxWidth: '1400px', margin: '0 auto' }}>
          {/* Search Section */}
          <div className="search-section">
            <h1 className="search-title">✈️ Find Your Perfect Flight</h1>

            {/* Trip Type */}
            <div className="trip-type-buttons">
              {['oneway', 'roundtrip', 'multicity'].map(type => (
                <button
                  key={type}
                  className={`trip-btn ${searchParams.tripType === type ? 'active' : ''}`}
                  onClick={() => setSearchParams({ ...searchParams, tripType: type })}
                >
                  {type === 'oneway' ? 'One Way' : type === 'roundtrip' ? 'Round Trip' : 'Multi City'}
                </button>
              ))}
            </div>

            {/* Search Form */}
            <form onSubmit={handleSearch}>
              <div className="form-grid">
                <div className="form-group">
                  <label className="form-label">From</label>
                  <input
                    type="text"
                    className="form-input"
                    value={searchParams.from}
                    onChange={(e) => setSearchParams({ ...searchParams, from: e.target.value })}
                    placeholder="Departure city"
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">To</label>
                  <input
                    type="text"
                    className="form-input"
                    value={searchParams.to}
                    onChange={(e) => setSearchParams({ ...searchParams, to: e.target.value })}
                    placeholder="Arrival city"
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Departure</label>
                  <input
                    type="date"
                    className="form-input"
                    value={searchParams.departure}
                    onChange={(e) => setSearchParams({ ...searchParams, departure: e.target.value })}
                  />
                </div>

                {searchParams.tripType === 'roundtrip' && (
                  <div className="form-group">
                    <label className="form-label">Return</label>
                    <input
                      type="date"
                      className="form-input"
                      value={searchParams.return}
                      onChange={(e) => setSearchParams({ ...searchParams, return: e.target.value })}
                    />
                  </div>
                )}

                <div className="form-group">
                  <label className="form-label">Passengers</label>
                  <select className="form-select" value={searchParams.passengers} onChange={(e) => setSearchParams({ ...searchParams, passengers: e.target.value })}>
                    <option>1 Adult</option>
                    <option>2 Adults</option>
                    <option>3 Adults</option>
                    <option>4+ Adults</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Class</label>
                  <select className="form-select" value={searchParams.class} onChange={(e) => setSearchParams({ ...searchParams, class: e.target.value })}>
                    <option value="economy">Economy</option>
                    <option value="business">Business</option>
                    <option value="premium">Premium</option>
                  </select>
                </div>
              </div>

              <button type="submit" className="search-button">
                🔍 Search Flights
              </button>
            </form>
          </div>

          {/* Results Section */}
          {hasSearched && (
            isLoading ? (
              <div className="loading-spinner">
                <div className="spinner"></div>
              </div>
            ) : sortedFlights.length > 0 ? (
              <div className="results-wrapper">
                {/* Sidebar Filters */}
                <div className="filters-sidebar">
                  <div className="filter-title">🎯 Filters</div>

                  <div className="filter-group">
                    <label className="filter-label">Price Range</label>
                    <input
                      type="range"
                      min="0"
                      max="15000"
                      value={filters.priceRange[1]}
                      onChange={(e) => setFilters({ ...filters, priceRange: [0, parseInt(e.target.value)] })}
                      className="price-range-input"
                    />
                    <div className="price-display">
                      ₹{filters.priceRange[0]} - ₹{filters.priceRange[1]}
                    </div>
                  </div>

                  <div className="filter-group">
                    <label className="filter-label">Stops</label>
                    <div className="radio-group">
                      {[
                        { value: 'all', label: 'All Flights' },
                        { value: 'nonstop', label: 'Non-Stop' },
                        { value: 'onestop', label: '1 Stop' },
                      ].map(option => (
                        <label key={option.value} className="radio-label">
                          <input
                            type="radio"
                            name="stops"
                            value={option.value}
                            checked={filters.stops === option.value}
                            onChange={(e) => setFilters({ ...filters, stops: e.target.value })}
                          />
                          {option.label}
                        </label>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Flight Results */}
                <div>
                  <div className="results-header">
                    📊 Showing {sortedFlights.length} flights
                  </div>
                  <div className="flights-results">
                    {sortedFlights.map(flight => (
                      <div key={flight.id} className="flight-card">
                        {/* Header */}
                        <div className="flight-header">
                          <div className="airline-info">
                            <div className="airline-logo">{flight.logo}</div>
                            <div className="airline-details">
                              <h3>{flight.airline}</h3>
                              <p>{flight.flightNumber}</p>
                            </div>
                          </div>
                          <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
                            <span className="discount-badge">{flight.discount}</span>
                            <span className="status-badge" style={{ background: `${flight.statusColor}20`, color: flight.statusColor }}>
                              {flight.liveStatus}
                            </span>
                          </div>
                        </div>

                        {/* Times */}
                        <div className="flight-times">
                          <div className="time-block">
                            <div className="time-value">{flight.departure}</div>
                            <div className="time-label">{flight.from}</div>
                          </div>

                          <div className="flight-duration">
                            <div className="duration-value">{flight.duration}</div>
                            <div className="duration-bar">
                              <div style={{ width: '15%', height: '2px', background: '#ddd' }}></div>
                              <div className="bar"></div>
                              <div style={{ width: '15%', height: '2px', background: '#ddd' }}></div>
                            </div>
                            {flight.stops > 0 && <div className="stops-info">{flight.stops} Stop{flight.stops > 1 ? 's' : ''}</div>}
                          </div>

                          <div className="time-block" style={{ textAlign: 'right' }}>
                            <div className="time-value">{flight.arrival}</div>
                            <div className="time-label">{flight.to}</div>
                          </div>
                        </div>

                        {/* Amenities */}
                        <div className="flight-amenities">
                          {flight.amenities.map((amenity, idx) => (
                            <div key={idx} className="amenity">{amenity}</div>
                          ))}
                        </div>

                        {/* Footer */}
                        <div className="flight-footer">
                          <div className="price-info">
                            <div className="price-label">Price per adult</div>
                            <div className="price-value">₹{flight.price.toLocaleString()}</div>
                          </div>
                          <button className="book-button" onClick={() => setSelectedFlight(flight)}>
                            Select ➜
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            ) : (
              <div className="empty-state">
                <div className="empty-state-icon">✈️</div>
                <div className="empty-state-title">No flights found</div>
                <div className="empty-state-text">Try adjusting your search criteria</div>
              </div>
            )
          )}
        </div>
      </div>
    </>
  );
}

export default FlightPage;