import React, { useState } from 'react';
import airIndiaExpress from '../assets/air.png';
import IndiGo from '../assets/Indigo.png';
import vistara from '../assets/vistara.jpg';
import spicejet from '../assets/spicejet.jpg';
import goFirst from '../assets/gofirst.png';
import airasia from '../assets/airasia.png';
import airIndia from '../assets/air.png';
import xyz from '../assets/xyz.jpg';

const FlightBookingPage = () => {
  const [searchParams, setSearchParams] = useState({
    tripType: 'oneway',
    from: 'New Delhi, India',
    to: 'Bengaluru, India',
    departure: '2026-02-10',
    return: '',
    passengers: 1,
    class: 'economy',
  });

  const [showResults, setShowResults] = useState(true);
  const [selectedSort, setSelectedSort] = useState('cheapest');
  const [selectedFlight, setSelectedFlight] = useState(null);
  const [appliedFilters, setAppliedFilters] = useState({
    nonStop: true,
    refundable: false,
    priceRange: [6500, 8000],
  });

  // Date slider state and logic
  const today = new Date();
  const dates = Array.from({ length: 8 }, (_, idx) => {
    const date = new Date(today);
    date.setDate(today.getDate() + idx);
    return date;
  });
  const [selectedIndex, setSelectedIndex] = useState(0);
  const formatDate = (date) => {
    const options = { weekday: 'short', month: 'short', day: 'numeric' };
    return date.toLocaleDateString('en-US', options);
  };
  const getPrice = (idx) => 6500 + idx * 100;
  const handleDateClick = (idx) => {
    setSelectedIndex(idx);
  };

  // Flight data with different local airline logos
  const flights = [
    {
      id: 1,
      airline: 'Air India Express',
      code: 'IX 1971',
      logo: airIndiaExpress,
      departure: '09:05',
      departureAirport: 'Ghaziabad',
      arrival: '11:55',
      arrivalAirport: 'Bengaluru',
      duration: '02 h 50 m',
      stops: 0,
      stopInfo: 'Non stop',
      price: 6848,
      duration_minutes: 170,
      refundable: false,
    },
    {
      id: 2,
      airline: 'IndiGo',
      code: '6E 2453',
      logo: IndiGo,
      departure: '10:30',
      departureAirport: 'New Delhi',
      arrival: '13:15',
      arrivalAirport: 'Bengaluru',
      duration: '02 h 45 m',
      stops: 0,
      stopInfo: 'Non stop',
      price: 6558,
      duration_minutes: 165,
      refundable: true,
    },
    {
      id: 3,
      airline: 'SpiceJet',
      code: 'SG 5234',
      logo: spicejet,
      departure: '06:15',
      departureAirport: 'New Delhi',
      arrival: '08:55',
      arrivalAirport: 'Bengaluru',
      duration: '02 h 40 m',
      stops: 0,
      stopInfo: 'Non stop',
      price: 6258,
      duration_minutes: 160,
      refundable: false,
    },
    {
      id: 4,
      airline: 'Vistara',
      code: 'UK 876',
      logo: vistara,
      departure: '14:00',
      departureAirport: 'New Delhi',
      arrival: '16:45',
      arrivalAirport: 'Bengaluru',
      duration: '02 h 45 m',
      stops: 0,
      stopInfo: 'Non stop',
      price: 7123,
      duration_minutes: 165,
      refundable: true,
    },
    {
      id: 5,
      airline: 'Go First',
      code: 'G8 4521',
      logo: goFirst,
      departure: '16:30',
      departureAirport: 'New Delhi',
      arrival: '19:20',
      arrivalAirport: 'Bengaluru',
      duration: '02 h 50 m',
      stops: 0,
      stopInfo: 'Non stop',
      price: 6848,
      duration_minutes: 170,
      refundable: false,
    },
    {
      id: 6,
      airline: 'AirAsia India',
      code: 'I5 9234',
      logo: airasia,
      departure: '18:45',
      departureAirport: 'New Delhi',
      arrival: '21:30',
      arrivalAirport: 'Bengaluru',
      duration: '02 h 45 m',
      stops: 1,
      stopInfo: '1 Stop',
      price: 5848,
      duration_minutes: 165,
      refundable: false,
    },
    {
      id: 7,
      airline: 'IndiGo',
      code: 'IG 6789',
      logo: IndiGo,
      departure: '12:15',
      departureAirport: 'New Delhi',
      arrival: '15:05',
      arrivalAirport: 'Bengaluru',
      duration: '02 h 50 m',
      stops: 0,
      stopInfo: 'Non stop',
      price: 7032,
      duration_minutes: 170,
      refundable: true,
    },
    {
      id: 8,
      airline: 'Air India',
      code: 'AI 5432',
      logo: airIndia,
      departure: '20:00',
      departureAirport: 'New Delhi',
      arrival: '22:50',
      arrivalAirport: 'Bengaluru',
      duration: '02 h 50 m',
      stops: 0,
      stopInfo: 'Non stop',
      price: 6948,
      duration_minutes: 170,
      refundable: false,
    },
  ];

  const filteredFlights = flights.filter(f => {
    if (appliedFilters.nonStop && f.stops > 0) return false;
    if (f.price < appliedFilters.priceRange[0] || f.price > appliedFilters.priceRange[1]) return false;
    if (appliedFilters.refundable && !f.refundable) return false;
    return true;
  });

  const sortedFlights = [...filteredFlights].sort((a, b) => {
    if (selectedSort === 'cheapest') return a.price - b.price;
    if (selectedSort === 'fastest') return a.duration_minutes - b.duration_minutes;
    return 0;
  });

  const handleSearch = (e) => {
    e.preventDefault();
    setShowResults(true);
  };

  const priceStats = {
    cheapest: Math.min(...flights.map(f => f.price)),
    fastest: flights.find(f => f.duration_minutes === Math.min(...flights.map(f => f.duration_minutes)))?.duration_minutes,
  };

  return (    
    <div style={{ backgroundColor: '#f5f7fa', minHeight: '100vh' }}>
      {/* Header */}
      <header style={{
        background: 'linear-gradient(135deg, #ffffff 0%, #f8f9fb 100%)',
        borderBottom: '1px solid #e8ecf1',
        padding: '16px 0',
        boxShadow: '0 2px 8px rgba(0,0,0,0.05)',
      }}>
        <div style={{ maxWidth: '1400px', margin: '0 auto', padding: '0 20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-start', marginBottom: '20px' }}>
            <div style={{ 
  display: 'flex',
  alignItems: 'center',
  gap: '10px',
  fontSize: '24px',
  fontWeight: 'bold',
  color: '#003580'
}}>
  <img 
    src={xyz}
    alt="Flight Booking Logo"
    style={{ 
      width: '62px',
      height: '62px',
      objectFit: 'contain'
    }}
  />
  Flight Booking
</div>

          </div>

          {/* Search Form */}
          <form onSubmit={handleSearch} style={{
            background: 'white',
            borderRadius: '8px',
            padding: '20px',
            display: 'grid',
            gridTemplateColumns: 'auto 1fr 1fr 1fr 1fr 1fr auto',
            gap: '15px',
            alignItems: 'end',
            boxShadow: '0 4px 12px rgba(0,0,0,0.08)',
          }}>
            {/* Trip Type */}
            <div style={{ display: 'flex', gap: '10px' }}>
              {['oneway', 'roundtrip', 'multicity'].map(type => (
                <label key={type} style={{ display: 'flex', alignItems: 'center', gap: '5px', cursor: 'pointer', fontSize: '13px' }}>
                  <input
                    type="radio"
                    name="tripType"
                    value={type}
                    checked={searchParams.tripType === type}
                    onChange={(e) => setSearchParams({ ...searchParams, tripType: e.target.value })}
                  />
                  {type === 'oneway' ? 'One Way' : type === 'roundtrip' ? 'Round Trip' : 'Multi City'}
                </label>
              ))}
            </div>

            {/* From */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>FROM</label>
              <input
                type="text"
                value={searchParams.from}
                onChange={(e) => setSearchParams({ ...searchParams, from: e.target.value })}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              />
            </div>

            {/* To */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>TO</label>
              <input
                type="text"
                value={searchParams.to}
                onChange={(e) => setSearchParams({ ...searchParams, to: e.target.value })}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              />
            </div>

            {/* Depart */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>DEPART</label>
              <input
                type="date"
                value={searchParams.departure}
                onChange={(e) => setSearchParams({ ...searchParams, departure: e.target.value })}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              />
            </div>

            {/* Return */}
            {searchParams.tripType === 'roundtrip' && (
              <div>
                <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>RETURN</label>
                <input
                  type="date"
                  value={searchParams.return}
                  onChange={(e) => setSearchParams({ ...searchParams, return: e.target.value })}
                  style={{
                    width: '100%',
                    padding: '12px',
                    border: '1px solid #ddd',
                    borderRadius: '4px',
                    fontSize: '14px',
                    boxSizing: 'border-box',
                  }}
                />
              </div>
            )}

            {/* Passengers & Class */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>PASSENGER & CLASS</label>
              <select
                value={`${searchParams.passengers}-${searchParams.class}`}
                onChange={(e) => {
                  const [p, c] = e.target.value.split('-');
                  setSearchParams({ ...searchParams, passengers: parseInt(p), class: c });
                }}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              >
                <option value="1-economy">1 Adult, Economy</option>
                <option value="1-premium">1 Adult, Premium</option>
                <option value="2-economy">2 Adults, Economy</option>
                <option value="2-business">2 Adults, Business</option>
              </select>
            </div>

            {/* Search Button */}
            <button
              type="submit"
              style={{
                padding: '12px 30px',
                background: '#003580',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontWeight: '600',
                fontSize: '14px',
              }}
            >
              SEARCH
            </button>
          </form>
        </div>
      </header>

      {/* Main Content */}
      {showResults && (
        <div style={{ maxWidth: '1400px', margin: '0 auto', padding: '30px 20px', display: 'grid', gridTemplateColumns: '280px 1fr', gap: '30px' }}>
          {/* Sidebar Filters */}
          <aside style={{ background: 'white', padding: '20px', borderRadius: '8px', height: 'fit-content' }}>
            <h3 style={{ fontSize: '16px', fontWeight: '600', marginBottom: '20px' }}>Applied Filters</h3>

            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer', fontSize: '14px' }}>
                <input
                  type="checkbox"
                  checked={appliedFilters.nonStop}
                  onChange={(e) => setAppliedFilters({ ...appliedFilters, nonStop: e.target.checked })}
                />
                <span>Non Stop</span>
                <span style={{ marginLeft: 'auto', color: '#666', fontSize: '13px' }}>₹{priceStats.cheapest.toLocaleString()}</span>
              </label>
            </div>

            <h3 style={{ fontSize: '16px', fontWeight: '600', marginBottom: '15px', marginTop: '25px' }}>Popular Filters</h3>

            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer', fontSize: '14px' }}>
                <input
                  type="checkbox"
                  checked={appliedFilters.refundable}
                  onChange={(e) => setAppliedFilters({ ...appliedFilters, refundable: e.target.checked })}
                />
                <span>Refundable Fares</span>
                <span style={{ marginLeft: 'auto', color: '#666', fontSize: '13px' }}>₹6,848</span>
              </label>
            </div>

            <h3 style={{ fontSize: '14px', fontWeight: '600', marginBottom: '15px', marginTop: '25px' }}>Price Range</h3>
            <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
              <input
                type="range"
                min="5000"
                max="10000"
                value={appliedFilters.priceRange[1]}
                onChange={(e) => setAppliedFilters({ ...appliedFilters, priceRange: [5000, parseInt(e.target.value)] })}
                style={{ flex: 1 }}
              />
              <span style={{ fontSize: '13px', fontWeight: '600' }}>₹{appliedFilters.priceRange[1].toLocaleString()}</span>
            </div>
          </aside>

          {/* Flight Results */}
          <main>
            {/* Promo Banners */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '15px', marginBottom: '30px' }}>
              <div style={{
                background: 'linear-gradient(135deg, #007bff 0%, #0056b3 100%)',
                color: 'white',
                padding: '20px',
                borderRadius: '8px',
                display: 'flex',
                alignItems: 'center',
                gap: '15px',
              }}>
                <span style={{ fontSize: '28px' }}>💳</span>
                <div>
                  <div style={{ fontSize: '13px', fontWeight: '600' }}>VISA Exclusive Offer</div>
                  <div style={{ fontSize: '12px', marginTop: '5px', opacity: '0.9' }}>Free Seat with VISA...</div>
                </div>
              </div>
              <div style={{
                background: 'linear-gradient(135deg, #dc3545 0%, #c82333 100%)',
                color: 'white',
                padding: '20px',
                borderRadius: '8px',
                display: 'flex',
                alignItems: 'center',
                gap: '15px',
              }}>
                <span style={{ fontSize: '28px' }}>🏦</span>
                <div>
                  <div style={{ fontSize: '13px', fontWeight: '600' }}>Flat 10% Instant Discount</div>
                  <div style={{ fontSize: '12px', marginTop: '5px', opacity: '0.9' }}>on IDFC FIRST Bank...</div>
                </div>
              </div>
              <div style={{
                background: 'linear-gradient(135deg, #6f42c1 0%, #5a32a3 100%)',
                color: 'white',
                padding: '20px',
                borderRadius: '8px',
                display: 'flex',
                alignItems: 'center',
                gap: '15px',
              }}>
                <span style={{ fontSize: '28px' }}>👥</span>
                <div>
                  <div style={{ fontSize: '13px', fontWeight: '600' }}>Meet and Greet</div>
                  <div style={{ fontSize: '12px', marginTop: '5px', opacity: '0.9' }}>Elevate your travel...</div>
                </div>
              </div>
            </div>

            <div style={{
      background: 'white',
      padding: '20px',
      borderRadius: '8px',
      marginBottom: '30px',
      overflowX: 'auto',
      display: 'flex',
      gap: '10px',
      alignItems: 'center',
    }}>
      <button style={{ background: '#f5f5f5', border: 'none', padding: '8px 12px', borderRadius: '4px', cursor: 'pointer' }}>←</button>
      {dates.map((date, idx) => (
        <button
          key={idx}
          onClick={() => handleDateClick(idx)}
          style={{
            padding: '12px 16px',
            border: '1px solid #ddd',
            background: idx === selectedIndex ? '#003580' : 'white',
            color: idx === selectedIndex ? 'white' : '#333',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '13px',
            fontWeight: '500',
            whiteSpace: 'nowrap',
            borderBottom: idx === selectedIndex ? '3px solid #003580' : 'none',
          }}
        >
          <div>{formatDate(date).split(',')[0]}</div>
          <div style={{ fontSize: '11px', opacity: '0.7' }}>₹{getPrice(idx).toLocaleString()}</div>
        </button>
      ))}
      <button style={{ background: '#f5f5f5', border: 'none', padding: '8px 12px', borderRadius: '4px', cursor: 'pointer' }}>→</button>
    </div>
            {/* Sort Options */}
            <div style={{
              background: 'white',
              padding: '20px',
              borderRadius: '8px',
              marginBottom: '20px',
              display: 'flex',
              gap: '15px',
              alignItems: 'center',
              borderBottom: '1px solid #eee',
              paddingBottom: '25px',
            }}>
              <span style={{ fontSize: '13px', fontWeight: '600', color: '#666' }}>Sort by:</span>
              {[
                { value: 'cheapest', label: 'CHEAPEST', price: '₹6,848 | 02h 50m' },
                { value: 'fastest', label: 'FASTEST', price: '02h 40m' },
                { value: 'preferred', label: 'YOU MAY PREFER', price: '₹6,848 | 02h 50m' },
              ].map(option => (
                <button
                  key={option.value}
                  onClick={() => setSelectedSort(option.value)}
                  style={{
                    padding: '12px 20px',
                    border: selectedSort === option.value ? '2px solid #003580' : '1px solid #ddd',
                    background: selectedSort === option.value ? '#f0f7ff' : 'white',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    fontSize: '13px',
                    fontWeight: '600',
                    color: selectedSort === option.value ? '#003580' : '#666',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                  }}
                >
                  <div>{option.label}</div>
                  <div style={{ fontSize: '11px', fontWeight: '400', marginTop: '4px', opacity: '0.7' }}>{option.price}</div>
                </button>
              ))}
            </div>

            {/* Flight Cards */}
            <div style={{ marginTop: '20px' }}>
              {sortedFlights.map((flight, idx) => (
                <div
                  key={flight.id}
                  onClick={() => setSelectedFlight(flight)}
                  style={{
                    background: 'white',
                    padding: '20px',
                    borderRadius: '8px',
                    marginBottom: '15px',
                    cursor: 'pointer',
                    border: selectedFlight?.id === flight.id ? '2px solid #003580' : '1px solid #e8ecf1',
                    transition: 'all 0.2s',
                    boxShadow: selectedFlight?.id === flight.id ? '0 4px 12px rgba(0,53,128,0.15)' : '0 2px 4px rgba(0,0,0,0.05)',
                  }}
                  onMouseEnter={(e) => e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.1)'}
                  onMouseLeave={(e) => e.currentTarget.style.boxShadow = selectedFlight?.id === flight.id ? '0 4px 12px rgba(0,53,128,0.15)' : '0 2px 4px rgba(0,0,0,0.05)'}
                >
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 3fr 1fr', gap: '30px', alignItems: 'center' }}>
                    {/* Airline Info */}
                    <div>
                      <div style={{
                        width: '70px',
                        height: '70px',
                        background: '#f5f5f5',
                        borderRadius: '8px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        marginBottom: '10px',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                        overflow: 'hidden',
                      }}>
                        <img 
                          src={flight.logo}
                          alt={flight.airline}
                          style={{
                            width: '100%',
                            height: '100%',
                            objectFit: 'contain',
                            padding: '5px',
                          }}
                          onError={(e) => {
                            e.target.style.display = 'none';
                            e.target.parentElement.textContent = flight.airline.substring(0, 2).toUpperCase();
                            e.target.parentElement.style.fontSize = '16px';
                            e.target.parentElement.style.fontWeight = 'bold';
                            e.target.parentElement.style.color = '#666';
                          }}
                        />
                      </div>
                      <div style={{ fontSize: '13px', fontWeight: '600', color: '#333', maxWidth: '70px', wordWrap: 'break-word' }}>{flight.airline}</div>
                      <div style={{ fontSize: '11px', color: '#999', marginTop: '3px' }}>{flight.code}</div>
                    </div>

                    {/* Flight Details */}
                    <div>
                      <div style={{ display: 'grid', gridTemplateColumns: 'auto 1fr auto', gap: '20px', alignItems: 'center', marginBottom: '15px' }}>
                        <div>
                          <div style={{ fontSize: '20px', fontWeight: '700', color: '#333' }}>{flight.departure}</div>
                          <div style={{ fontSize: '12px', color: '#999', marginTop: '4px' }}>{flight.departureAirport}</div>
                          <div style={{ fontSize: '11px', color: '#666', marginTop: '2px', fontWeight: '500' }}>({flight.departureAirport === 'New Delhi' ? 'IGI' : 'NAG'} Airport)</div>
                        </div>

                        <div style={{ textAlign: 'center' }}>
                          <div style={{ fontSize: '11px', color: '#999', marginBottom: '8px' }}>{flight.duration}</div>
                          <div style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '8px',
                            justifyContent: 'center',
                            marginBottom: '6px',
                          }}>
                            <div style={{ height: '2px', width: '30px', background: '#ddd' }}></div>
                            {flight.stops === 0 ? <span style={{ fontSize: '12px' }}>✈️</span> : <span style={{ fontSize: '12px', color: '#ff6b6b', fontWeight: '600' }}>1</span>}
                            <div style={{ height: '2px', width: '30px', background: '#ddd' }}></div>
                          </div>
                          <div style={{ fontSize: '12px', fontWeight: '600', color: flight.stops === 0 ? '#28a745' : '#ff6b6b' }}>
                            {flight.stopInfo}
                          </div>
                        </div>

                        <div style={{ textAlign: 'right' }}>
                          <div style={{ fontSize: '20px', fontWeight: '700', color: '#333' }}>{flight.arrival}</div>
                          <div style={{ fontSize: '12px', color: '#999', marginTop: '4px' }}>{flight.arrivalAirport}</div>
                          <div style={{ fontSize: '11px', color: '#666', marginTop: '2px', fontWeight: '500' }}>(BLR Airport)</div>
                        </div>
                      </div>
                    </div>

                    {/* Price and Action */}
                    <div style={{ textAlign: 'right' }}>
                      <div style={{ fontSize: '24px', fontWeight: '700', color: '#003580', marginBottom: '10px' }}>
                        ₹{flight.price.toLocaleString()}
                      </div>
                      <div style={{ fontSize: '11px', color: '#999', marginBottom: '12px' }}>/adult</div>
                      <button style={{
                        padding: '10px 16px',
                        background: 'white',
                        color: '#003580',
                        border: '2px solid #003580',
                        borderRadius: '4px',
                        cursor: 'pointer',
                        fontWeight: '600',
                        fontSize: '12px',
                      }}>
                        VIEW PRICES
                      </button>
                    </div>
                  </div>

                  {/* Footer */}
                  <div style={{
                    marginTop: '15px',
                    paddingTop: '15px',
                    borderTop: '1px solid #f0f0f0',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                  }}>
                    <div style={{ fontSize: '11px', color: '#666' }}>
                      {flight.refundable && <span style={{ color: '#28a745', fontWeight: '600' }}>✓ Refundable</span>}
                    </div>
                    <div style={{ fontSize: '11px', color: '#003580', cursor: 'pointer', fontWeight: '600' }}>
                      🔒 Lock this price @ ₹413 →
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </main>
        </div>
      )}
    </div>
  );
};

export default FlightBookingPage;