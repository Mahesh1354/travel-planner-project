import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { 
  Search, MapPin, Calendar, Users, Clock, Star, Heart, Filter, 
  ChevronRight, TrendingUp, AlertCircle, CheckCircle2, XCircle, Eye, EyeOff 
} from 'lucide-react';
import xyzb from '../assets/rail.png';

export default function TrainsPage() {
  const [searchParams, setSearchParams] = useState({
    source: '',
    destination: '',
    departDate: '',
    returnDate: '',
    passengers: '1',
    seatClass: 'economy'
  });

  const [trains, setTrains] = useState([]);
  const [filteredTrains, setFilteredTrains] = useState([]);
  const [favorites, setFavorites] = useState({});
  const [selectedSort, setSelectedSort] = useState('price');
  const [selectedFilters, setSelectedFilters] = useState({});
  const [priceRange, setPriceRange] = useState({ min: 0, max: 10000 });
  const [selectedTrain, setSelectedTrain] = useState(null);
  const [loading, setLoading] = useState(false);
  const [searchResultsCount, setSearchResultsCount] = useState(0);

  // Sample trains data
 const sampleTrains = [
  {
    id: 1,
    name: 'Express 101',
    source: 'Mumbai',
    destination: 'Delhi',
    departTime: '22:00',
    arrivalTime: '08:30',
    duration: '10h 30m',
    distance: '1384 km',
    price: 2500,
    originalPrice: 3500,
    rating: 4.8,
    reviews: 1240,
    availability: 45,
    type: 'express',
    amenities: ['AC', 'WiFi', 'Food', 'Charging'],
    seats: [
      { id: 1, class: 'economy', price: 2500, available: 45 },
      { id: 2, class: 'comfort', price: 3500, available: 28 },
      { id: 3, class: 'luxury', price: 5000, available: 12 }
    ],
    operator: 'Indian Railways',
    image: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSMDu1orEfxQKvLt3hJTa3k748OolF4wxDkdg&s'
  },
  {
    id: 2,
    name: 'Rajdhani Express',
    source: 'Mumbai',
    destination: 'Delhi',
    departTime: '16:30',
    arrivalTime: '06:00',
    duration: '13h 30m',
    distance: '1384 km',
    price: 4200,
    originalPrice: 5500,
    rating: 4.9,
    reviews: 2156,
    availability: 12,
    type: 'premium',
    amenities: ['AC', 'WiFi', 'Food', 'Charging', 'Bedding'],
    seats: [
      { id: 1, class: 'comfort', price: 4200, available: 12 },
      { id: 2, class: 'luxury', price: 6500, available: 8 }
    ],
    operator: 'Indian Railways',
    image: 'https://aggrp.in/wp-content/uploads/2022/10/AG-Bloges-Toy-Trains-1024x576.png'
  },
  {
    id: 3,
    name: 'Shatabdi Express',
    source: 'Mumbai',
    destination: 'Delhi',
    departTime: '06:00',
    arrivalTime: '14:00',
    duration: '8h',
    distance: '1384 km',
    price: 3800,
    originalPrice: 4800,
    rating: 4.7,
    reviews: 1890,
    availability: 34,
    type: 'super-fast',
    amenities: ['AC', 'WiFi', 'Food', 'Charging'],
    seats: [
      { id: 1, class: 'economy', price: 3800, available: 34 },
      { id: 2, class: 'comfort', price: 4800, available: 18 }
    ],
    operator: 'Indian Railways',
    image: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0GwdklPaH9LS6-pgFe_6TkogGLdWrMfHnFw&s'
  },
  {
    id: 4,
    name: 'Local Express',
    source: 'Mumbai',
    destination: 'Delhi',
    departTime: '12:00',
    arrivalTime: '22:30',
    duration: '10h 30m',
    distance: '1384 km',
    price: 1800,
    originalPrice: 2500,
    rating: 4.5,
    reviews: 876,
    availability: 67,
    type: 'local',
    amenities: ['AC', 'Food'],
    seats: [
      { id: 1, class: 'economy', price: 1800, available: 67 }
    ],
    operator: 'Indian Railways',
    image: 'https://www.explore.com/img/gallery/the-oldest-vintage-trains-around-the-world-you-can-still-ride-today/l-intro-1725233481.jpg'
  },
  {
    id: 5,
    name: 'Premium Express',
    source: 'Mumbai',
    destination: 'Delhi',
    departTime: '20:00',
    arrivalTime: '06:00',
    duration: '10h',
    distance: '1384 km',
    price: 5200,
    originalPrice: 6800,
    rating: 4.9,
    reviews: 1456,
    availability: 8,
    type: 'premium',
    amenities: ['AC', 'WiFi', 'Food', 'Charging', 'Bedding', 'Lounge'],
    seats: [
      { id: 1, class: 'luxury', price: 5200, available: 8 }
    ],
    operator: 'Private Railways',
    image: 'https://cdn.britannica.com/28/233928-050-CC617C2B/high-speed-railway-commuter-train.jpg'
  },
  {
    id: 6,
    name: 'Night Traveler',
    source: 'Mumbai',
    destination: 'Delhi',
    departTime: '23:30',
    arrivalTime: '09:30',
    duration: '10h',
    distance: '1384 km',
    price: 2800,
    originalPrice: 3800,
    rating: 4.6,
    reviews: 1123,
    availability: 42,
    type: 'express',
    amenities: ['AC', 'WiFi', 'Food', 'Charging', 'Bedding'],
    seats: [
      { id: 1, class: 'economy', price: 2800, available: 42 },
      { id: 2, class: 'comfort', price: 3800, available: 25 }
    ],
    operator: 'Indian Railways',
    image: 'https://img-cdn.publive.online/filters:format(webp)/english-betterindia/media/post_attachments/uploads/2017/09/IR-1.jpg'
  },
  {
    id: 7,
    name: 'Golden Chariot',
    source: 'Mumbai',
    destination: 'Delhi',
    departTime: '18:00',
    arrivalTime: '11:00',
    duration: '17h',
    distance: '1384 km',
    price: 8500,
    originalPrice: 10000,
    rating: 5.0,
    reviews: 450,
    availability: 5,
    type: 'luxury',
    amenities: ['AC', 'WiFi', 'Food', 'Charging', 'Bedding', 'Lounge', 'Shower'],
    seats: [
      { id: 1, class: 'luxury', price: 8500, available: 5 }
    ],
    operator: 'Heritage Tours',
    image: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSCcWbiaIeoaUQ22IA-2gNv2gY5pCb04WEu-Q&s'
  },
  {
    id: 8,
    name: 'Deccan Odyssey',
    source: 'Mumbai',
    destination: 'Delhi',
    departTime: '14:00',
    arrivalTime: '04:00',
    duration: '14h',
    distance: '1384 km',
    price: 3100,
    originalPrice: 4200,
    rating: 4.4,
    reviews: 980,
    availability: 18,
    type: 'express',
    amenities: ['AC', 'Food', 'Charging'],
    seats: [
      { id: 1, class: 'economy', price: 3100, available: 18 },
      { id: 2, class: 'comfort', price: 4500, available: 10 }
    ],
    operator: 'Indian Railways',
    image: 'https://sundayguardianlive.com/wp-content/uploads/2026/01/five-new-amrit-bharat-express-trains-launched-by-pm-modi.jpg'
  },
  {
    id: 9,
    name: 'Tejas Express',
    source: 'Mumbai',
    destination: 'Delhi',
    departTime: '09:00',
    arrivalTime: '17:30',
    duration: '8h 30m',
    distance: '1384 km',
    price: 3600,
    originalPrice: 4500,
    rating: 4.8,
    reviews: 1320,
    availability: 22,
    type: 'super-fast',
    amenities: ['AC', 'WiFi', 'Food', 'Charging', 'TV'],
    seats: [
      { id: 1, class: 'comfort', price: 3600, available: 12 },
      { id: 2, class: 'luxury', price: 5800, available: 10 }
    ],
    operator: 'Private Railways',
    image: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSEvzUpNEQVb0X1udntOlgKbpAanZ-H7UnsZA&s'
  },
  {
    id: 10,
    name: 'Duronto Express',
    source: 'Mumbai',
    destination: 'Delhi',
    departTime: '11:15',
    arrivalTime: '02:45',
    duration: '15h 30m',
    distance: '1384 km',
    price: 2900,
    originalPrice: 3800,
    rating: 4.5,
    reviews: 2100,
    availability: 55,
    type: 'express',
    amenities: ['AC', 'Food', 'Charging', 'Bedding'],
    seats: [
      { id: 1, class: 'economy', price: 2900, available: 55 }
    ],
    operator: 'Indian Railways',
    image: 'https://thecsrjournal.in/wp-content/uploads/2025/11/vande-bharat-2.jpg'
  }
];

  useEffect(() => {
    setTrains(sampleTrains);
    setFilteredTrains(sampleTrains);
  }, []);

  const handleSearchChange = (e) => {
    const { name, value } = e.target;
    setSearchParams(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSearch = () => {
    setLoading(true);
    setTimeout(() => {
      let filtered = trains.filter(train => {
        if (searchParams.source && !train.source.toLowerCase().includes(searchParams.source.toLowerCase())) {
          return false;
        }
        if (searchParams.destination && !train.destination.toLowerCase().includes(searchParams.destination.toLowerCase())) {
          return false;
        }
        if (train.price < priceRange.min || train.price > priceRange.max) {
          return false;
        }
        if (selectedFilters.express && train.type !== 'express') return false;
        if (selectedFilters.premium && train.type !== 'premium') return false;
        if (selectedFilters.superfast && train.type !== 'super-fast') return false;
        return true;
      });

      filtered.sort((a, b) => {
        if (selectedSort === 'price') return a.price - b.price;
        if (selectedSort === 'duration') return parseFloat(a.duration) - parseFloat(b.duration);
        if (selectedSort === 'rating') return b.rating - a.rating;
        return b.reviews - a.reviews;
      });

      setFilteredTrains(filtered);
      setSearchResultsCount(filtered.length);
      setLoading(false);
    }, 500);
  };

  const toggleFavorite = (id) => {
    setFavorites(prev => ({
      ...prev,
      [id]: !prev[id]
    }));
  };

  const toggleFilter = (filter) => {
    setSelectedFilters(prev => ({
      ...prev,
      [filter]: !prev[filter]
    }));
  };

  const discountPercent = (train) => {
    return Math.round(((train.originalPrice - train.price) / train.originalPrice) * 100);
  };

  const getDurationHours = (duration) => {
    const match = duration.match(/(\d+)h\s*(?:(\d+)m)?/);
    return match ? (parseInt(match[1]) + (parseInt(match[2]) || 0) / 60) : 0;
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
              <div style={{
                width: '50px',
                height: '50px',
                backgroundColor: '#003580',
                borderRadius: '8px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: 'white',
                fontWeight: 'bold',
                fontSize: '28px'
              }}>
                 <img 
                                    src={xyzb}
                                    alt="Flight Booking Logo"
                                    style={{ 
                                      width: '62px',
                                      height: '62px',
                                      objectFit: 'contain'
                                    }}
                                  />
              </div>
              <h1 style={{ margin: 0, fontSize: '32px', fontWeight: 'bold' }}>Train Tickets</h1>
            </div>
          </div>

          {/* Search Form */}
          <form style={{
            background: 'white',
            borderRadius: '8px',
            padding: '20px',
            display: 'grid',
            gridTemplateColumns: '1fr 1fr 1fr 1fr 1fr auto',
            gap: '15px',
            alignItems: 'end',
            boxShadow: '0 4px 12px rgba(0,0,0,0.08)',
          }}>
            {/* Source */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>
                FROM
              </label>
              <input
                type="text"
                name="source"
                value={searchParams.source}
                onChange={handleSearchChange}
                placeholder="Departure city"
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

            {/* Destination */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>
                TO
              </label>
              <input
                type="text"
                name="destination"
                value={searchParams.destination}
                onChange={handleSearchChange}
                placeholder="Arrival city"
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

            {/* Depart Date */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>
                DEPART
              </label>
              <input
                type="date"
                name="departDate"
                value={searchParams.departDate}
                onChange={handleSearchChange}
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

            {/* Passengers */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>
                PASSENGERS
              </label>
              <select
                name="passengers"
                value={searchParams.passengers}
                onChange={handleSearchChange}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              >
                <option value="1">1 Passenger</option>
                <option value="2">2 Passengers</option>
                <option value="3">3 Passengers</option>
                <option value="4">4 Passengers</option>
                <option value="5">5 Passengers</option>
              </select>
            </div>

            {/* Class */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>
                CLASS
              </label>
              <select
                name="seatClass"
                value={searchParams.seatClass}
                onChange={handleSearchChange}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              >
                <option value="economy">Economy</option>
                <option value="comfort">Comfort</option>
                <option value="luxury">Luxury</option>
              </select>
            </div>

            {/* Search Button */}
            <button
              type="submit"
              onClick={(e) => {
                e.preventDefault();
                handleSearch();
              }}
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
      <div style={{ maxWidth: '1400px', margin: '0 auto', padding: '30px 20px', display: 'grid', gridTemplateColumns: '280px 1fr', gap: '30px' }}>
        {/* Sidebar Filters */}
        <aside style={{ background: 'white', padding: '20px', borderRadius: '8px', height: 'fit-content', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
          <h3 style={{ fontSize: '16px', fontWeight: '600', marginBottom: '20px' }}>Filters</h3>

          {/* Train Type */}
          <div style={{ marginBottom: '25px' }}>
            <h4 style={{ fontSize: '13px', fontWeight: '600', color: '#666', marginBottom: '12px', textTransform: 'uppercase' }}>
              Train Type
            </h4>
            {[
              { id: 'express', label: 'Express' },
              { id: 'premium', label: 'Premium' },
              { id: 'superfast', label: 'Super Fast' }
            ].map(filter => (
              <label key={filter.id} style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', marginBottom: '10px', fontSize: '13px' }}>
                <input
                  type="checkbox"
                  checked={selectedFilters[filter.id] || false}
                  onChange={() => toggleFilter(filter.id)}
                  style={{ cursor: 'pointer' }}
                />
                {filter.label}
              </label>
            ))}
          </div>

          {/* Price Range */}
          <div style={{ marginBottom: '25px', paddingTop: '20px', borderTop: '1px solid #eee' }}>
            <h4 style={{ fontSize: '13px', fontWeight: '600', color: '#666', marginBottom: '12px', textTransform: 'uppercase' }}>
              Price Range
            </h4>
            <div style={{ marginBottom: '12px' }}>
              <input 
                type="range" 
                min="0" 
                max="10000" 
                step="500"
                value={priceRange.max} 
                onChange={(e) => setPriceRange({ ...priceRange, max: parseInt(e.target.value) })} 
                style={{ width: '100%', cursor: 'pointer' }}
              />
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', color: '#666' }}>
              <span>₹{priceRange.min.toLocaleString()}</span>
              <span>₹{priceRange.max.toLocaleString()}</span>
            </div>
          </div>

          {/* Departure Time */}
          <div style={{ paddingTop: '20px', borderTop: '1px solid #eee' }}>
            <h4 style={{ fontSize: '13px', fontWeight: '600', color: '#666', marginBottom: '12px', textTransform: 'uppercase' }}>
              Departure
            </h4>
            {['Morning (6AM-12PM)', 'Afternoon (12PM-6PM)', 'Evening (6PM-12AM)', 'Night (12AM-6AM)'].map((time, i) => (
              <label key={i} style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', marginBottom: '10px', fontSize: '13px' }}>
                <input type="checkbox" style={{ cursor: 'pointer' }} />
                {time}
              </label>
            ))}
          </div>
        </aside>

        {/* Main Content */}
        <main>
          {/* Results Header */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '25px' }}>
            <div>
              <h2 style={{ fontSize: '24px', fontWeight: '700', color: '#0f172a', marginBottom: '5px' }}>
                {searchResultsCount > 0 ? searchResultsCount : filteredTrains.length} Trains Found
              </h2>
              <p style={{ color: '#666', fontSize: '13px' }}>
                {searchParams.source && searchParams.destination 
                  ? `${searchParams.source} → ${searchParams.destination}` 
                  : 'Select source and destination to search'}
              </p>
            </div>
            <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
              <span style={{ fontSize: '12px', color: '#666', fontWeight: '500' }}>Sort by:</span>
              <select
                value={selectedSort}
                onChange={(e) => setSelectedSort(e.target.value)}
                style={{
                  padding: '8px 12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '13px',
                  cursor: 'pointer'
                }}
              >
                <option value="popularity">Popularity</option>
                <option value="price">Price (Low to High)</option>
                <option value="duration">Duration</option>
                <option value="rating">Rating</option>
              </select>
            </div>
          </div>

          {/* Trains List */}
          <div style={{ display: 'grid', gap: '20px' }}>
            {loading ? (
              <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
                <p>Loading trains...</p>
              </div>
            ) : filteredTrains.length > 0 ? (
              filteredTrains.map(train => (
                <div
                  key={train.id}
                  onClick={() => setSelectedTrain(train)}
                  style={{
                    background: 'white',
                    borderRadius: '8px',
                    overflow: 'hidden',
                    border: '1px solid #e8ecf1',
                    transition: 'all 0.3s',
                    cursor: 'pointer',
                    boxShadow: '0 2px 4px rgba(0,0,0,0.05)',
                    display: 'grid',
                    gridTemplateColumns: 'auto 1fr auto',
                    gap: '20px',
                    padding: '20px'
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.boxShadow = '0 8px 16px rgba(0,0,0,0.12)';
                    e.currentTarget.style.transform = 'translateY(-4px)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.boxShadow = '0 2px 4px rgba(0,0,0,0.05)';
                    e.currentTarget.style.transform = 'translateY(0)';
                  }}
                >
                  {/* Train Image */}
                  <div style={{ position: 'relative', width: '180px', height: '120px', borderRadius: '8px', overflow: 'hidden' }}>
                    <img
                      src={train.image}
                      alt={train.name}
                      style={{
                        width: '100%',
                        height: '100%',
                        objectFit: 'cover'
                      }}
                    />
                    {train.originalPrice && (
                      <div style={{
                        position: 'absolute',
                        top: '10px',
                        right: '10px',
                        background: '#ff6b6b',
                        color: 'white',
                        padding: '4px 8px',
                        borderRadius: '4px',
                        fontSize: '11px',
                        fontWeight: '600'
                      }}>
                        -{discountPercent(train)}%
                      </div>
                    )}
                  </div>

                  {/* Train Details */}
                  <div>
                    <div style={{ marginBottom: '15px' }}>
                      <h3 style={{ fontSize: '16px', fontWeight: '700', color: '#0f172a', marginBottom: '8px' }}>
                        {train.name}
                      </h3>
                      <div style={{ display: 'flex', gap: '15px', alignItems: 'center', marginBottom: '12px' }}>
                        <div>
                          <div style={{ fontSize: '18px', fontWeight: '700', color: '#667eea' }}>
                            {train.departTime}
                          </div>
                          <div style={{ fontSize: '11px', color: '#999' }}>{train.source}</div>
                        </div>
                        <div style={{ textAlign: 'center', flex: 1 }}>
                          <div style={{ fontSize: '12px', color: '#666', marginBottom: '4px' }}>
                            {train.duration}
                          </div>
                          <div style={{ borderTop: '1px solid #ddd', width: '100%', margin: '4px 0' }}></div>
                          <div style={{ fontSize: '10px', color: '#999' }}>
                            {train.distance}
                          </div>
                        </div>
                        <div>
                          <div style={{ fontSize: '18px', fontWeight: '700', color: '#667eea' }}>
                            {train.arrivalTime}
                          </div>
                          <div style={{ fontSize: '11px', color: '#999' }}>{train.destination}</div>
                        </div>
                      </div>
                      <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                        {train.amenities.slice(0, 3).map((amenity, i) => (
                          <span key={i} style={{
                            fontSize: '11px',
                            background: '#f0f0f0',
                            color: '#666',
                            padding: '4px 8px',
                            borderRadius: '12px'
                          }}>
                            {amenity}
                          </span>
                        ))}
                      </div>
                    </div>

                    {/* Rating */}
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      {[...Array(5)].map((_, i) => (
                        <span key={i} style={{
                          color: i < Math.floor(train.rating) ? '#fbbf24' : '#e5e7eb',
                          fontSize: '12px'
                        }}>
                          ★
                        </span>
                      ))}
                      <span style={{ fontSize: '12px', color: '#666' }}>
                        {train.rating} ({train.reviews} reviews)
                      </span>
                    </div>
                  </div>

                  {/* Price & Button */}
                  <div style={{ textAlign: 'right', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', minWidth: '150px' }}>
                    <div>
                      <div style={{ marginBottom: '8px' }}>
                        {train.originalPrice && (
                          <div style={{ fontSize: '12px', color: '#999', textDecoration: 'line-through', marginBottom: '4px' }}>
                            ₹{train.originalPrice.toLocaleString()}
                          </div>
                        )}
                        <div style={{ fontSize: '22px', fontWeight: '700', color: '#667eea', marginBottom: '4px' }}>
                          ₹{train.price.toLocaleString()}
                        </div>
                        <div style={{ fontSize: '10px', color: '#666' }}>per person</div>
                      </div>
                    </div>
                    <div style={{ display: 'flex', gap: '10px' }}>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          toggleFavorite(train.id);
                        }}
                        style={{
                          width: '40px',
                          height: '40px',
                          border: '1px solid #ddd',
                          borderRadius: '50%',
                          background: 'white',
                          cursor: 'pointer',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          transition: 'all 0.2s'
                        }}
                      >
                        <Heart
                          size={18}
                          fill={favorites[train.id] ? '#ff6b6b' : 'none'}
                          color={favorites[train.id] ? '#ff6b6b' : '#999'}
                        />
                      </button>
                      <button
                        onClick={(e) => e.stopPropagation()}
                        style={{
                          flex: 1,
                          padding: '10px',
                          background: '#003580',
                          color: 'white',
                          border: 'none',
                          borderRadius: '4px',
                          cursor: 'pointer',
                          fontWeight: '600',
                          fontSize: '12px',
                          transition: 'all 0.2s'
                        }}
                        onMouseEnter={(e) => e.target.style.transform = 'scale(1.05)'}
                        onMouseLeave={(e) => e.target.style.transform = 'scale(1)'}
                      >
                        Book Now
                      </button>
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
                <AlertCircle size={48} style={{ margin: '0 auto 15px', color: '#999' }} />
                <p style={{ fontSize: '16px' }}>No trains found. Try different search criteria.</p>
              </div>
            )}
          </div>
        </main>
      </div>

      {/* Train Details Modal */}
      {selectedTrain && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.7)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000,
          padding: '20px',
          overflowY: 'auto'
        }}
        onClick={() => setSelectedTrain(null)}>
          <div
            onClick={(e) => e.stopPropagation()}
            style={{
              backgroundColor: 'white',
              borderRadius: '8px',
              maxWidth: '800px',
              width: '100%',
              maxHeight: '90vh',
              overflowY: 'auto',
              position: 'relative'
            }}>
            
            {/* Close Button */}
            <button
              onClick={() => setSelectedTrain(null)}
              style={{
                position: 'absolute',
                top: '15px',
                right: '15px',
                background: 'white',
                border: '1px solid #ddd',
                borderRadius: '50%',
                width: '36px',
                height: '36px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer',
                zIndex: 10,
                fontSize: '18px',
                fontWeight: 'bold'
              }}>
              ✕
            </button>

            {/* Modal Content */}
            <div style={{ padding: '30px' }}>
              {/* Train Header */}
              <h2 style={{ fontSize: '28px', fontWeight: '700', color: '#0f172a', marginBottom: '15px' }}>
                {selectedTrain.name}
              </h2>

              {/* Journey Details */}
              <div style={{
                background: '#f5f7fa',
                padding: '20px',
                borderRadius: '8px',
                marginBottom: '25px'
              }}>
                <div style={{ display: 'grid', gridTemplateColumns: 'auto 1fr auto', gap: '30px', alignItems: 'center' }}>
                  <div>
                    <div style={{ fontSize: '28px', fontWeight: '700', color: '#667eea' }}>
                      {selectedTrain.departTime}
                    </div>
                    <div style={{ fontSize: '12px', color: '#666', marginTop: '5px' }}>
                      {selectedTrain.source}
                    </div>
                  </div>
                  <div style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: '12px', color: '#666', marginBottom: '8px' }}>
                      {selectedTrain.duration}
                    </div>
                    <div style={{ borderTop: '2px solid #667eea', margin: '0 20px' }}></div>
                    <div style={{ fontSize: '11px', color: '#666', marginTop: '8px' }}>
                      {selectedTrain.distance}
                    </div>
                  </div>
                  <div>
                    <div style={{ fontSize: '28px', fontWeight: '700', color: '#667eea' }}>
                      {selectedTrain.arrivalTime}
                    </div>
                    <div style={{ fontSize: '12px', color: '#666', marginTop: '5px' }}>
                      {selectedTrain.destination}
                    </div>
                  </div>
                </div>
              </div>

              {/* Details Grid */}
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '20px', marginBottom: '25px' }}>
                <div>
                  <h4 style={{ fontSize: '12px', fontWeight: '600', color: '#666', marginBottom: '8px', textTransform: 'uppercase' }}>
                    Train Operator
                  </h4>
                  <p style={{ fontSize: '14px', color: '#0f172a' }}>{selectedTrain.operator}</p>
                </div>
                <div>
                  <h4 style={{ fontSize: '12px', fontWeight: '600', color: '#666', marginBottom: '8px', textTransform: 'uppercase' }}>
                    Available Seats
                  </h4>
                  <p style={{ fontSize: '14px', color: '#0f172a' }}>{selectedTrain.availability} seats</p>
                </div>
              </div>

              {/* Amenities */}
              <div style={{ marginBottom: '25px' }}>
                <h4 style={{ fontSize: '14px', fontWeight: '600', color: '#0f172a', marginBottom: '12px' }}>
                  Amenities
                </h4>
                <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                  {selectedTrain.amenities.map((amenity, i) => (
                    <span key={i} style={{
                      fontSize: '12px',
                      background: '#667eea',
                      color: 'white',
                      padding: '6px 12px',
                      borderRadius: '20px'
                    }}>
                      {amenity}
                    </span>
                  ))}
                </div>
              </div>

              {/* Seat Classes */}
              <div style={{ marginBottom: '25px' }}>
                <h4 style={{ fontSize: '14px', fontWeight: '600', color: '#0f172a', marginBottom: '12px' }}>
                  Available Classes
                </h4>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '12px' }}>
                  {selectedTrain.seats.map(seat => (
                    <div key={seat.id} style={{
                      padding: '15px',
                      border: '1px solid #e8ecf1',
                      borderRadius: '8px',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center'
                    }}>
                      <div>
                        <div style={{ fontSize: '12px', fontWeight: '600', color: '#0f172a', textTransform: 'capitalize' }}>
                          {seat.class}
                        </div>
                        <div style={{ fontSize: '11px', color: '#666', marginTop: '4px' }}>
                          {seat.available} available
                        </div>
                      </div>
                      <div style={{ fontSize: '16px', fontWeight: '700', color: '#667eea' }}>
                        ₹{seat.price.toLocaleString()}
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Booking Button */}
              <button style={{
                width: '100%',
                padding: '15px',
                background: '#003580',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontWeight: '600',
                fontSize: '16px',
                transition: 'all 0.3s'
              }}
              onMouseEnter={(e) => e.target.style.transform = 'translateY(-2px)'}
              onMouseLeave={(e) => e.target.style.transform = 'translateY(0)'}
              >
                PROCEED TO BOOKING
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}