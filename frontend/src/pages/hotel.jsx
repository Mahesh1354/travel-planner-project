import React, { useState, useMemo } from 'react';
import { MapPin, Star, MapPinIcon, Users, Calendar, Search, Heart, ChevronRight, X, MessageSquare, ThumbsUp } from 'lucide-react';
import xyzs from '../assets/icons.jpg';
export default function HotelsPage() {
  const [destination, setDestination] = useState('');
  const [checkIn, setCheckIn] = useState('Wed, 11 Feb 2026');
  const [checkOut, setCheckOut] = useState('Thu, 12 Feb 2026');
  const [rooms, setRooms] = useState('1');
  const [searchLocality, setSearchLocality] = useState('');
  const [selectedSort, setSelectedSort] = useState('popularity');
  const [favorites, setFavorites] = useState({});
  const [selectedFilters, setSelectedFilters] = useState({});
  const [priceRange, setPriceRange] = useState({ min: 0, max: 100000 });
  const [selectedHotel, setSelectedHotel] = useState(null);

  const allHotels = [
    {
      id: 1,
      name: 'Taj Cidade de Goa Heritage',
      location: 'Goa, India',
      country: 'India',
      area: 'North Goa',
      rating: 4.6,
      reviews: 2026,
      price: 20000,
      taxes: 3600,
      image: 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&q=80&w=800',
      badge: 'SPONSORED',
      amenities: ['Breakfast', 'Pool', 'Beach', 'Spa', 'Restaurant'],
      stars: '5',
      type: '5star',
      fullDescription: 'Taj Cidade de Goa Heritage is a luxurious 5-star hotel offering beachfront paradise with world-class amenities. Located on the pristine Vainguinim Beach, this heritage property seamlessly blends Portuguese architecture with modern luxury.',
      images: [
        'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1540541338287-41700207dee6?auto=format&fit=crop&q=80&w=1200'
      ],
      comments: [
        { name: 'Rajesh Kumar', rating: 5, text: 'Absolutely amazing! The beach view from my room was breathtaking.', date: '2 days ago', helpful: 245 },
        { name: 'Sarah Johnson', rating: 4, text: 'Great location and excellent service.', date: '1 week ago', helpful: 156 },
        { name: 'Priya Sharma', rating: 5, text: 'Best honeymoon destination!', date: '10 days ago', helpful: 389 }
      ]
    },
    {
      id: 2,
      name: 'Ginger Goa, Candolim',
      location: 'Goa, India',
      country: 'India',
      area: 'Candolim',
      rating: 4.3,
      reviews: 2271,
      price: 4500,
      taxes: 810,
      image: 'https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?auto=format&fit=crop&q=80&w=800',
      amenities: ['WiFi', 'AC', 'Restaurant', 'Gym', 'Hot Water'],
      stars: '4',
      type: 'budget',
      fullDescription: 'Ginger Goa is a budget-friendly, contemporary hotel designed for the modern traveler. Located just 9 minutes walk from Candolim Beach.',
      images: [
        'https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1590490359683-658d3d23f972?auto=format&fit=crop&q=80&w=1200'
      ],
      comments: [
        { name: 'Michael Chen', rating: 4, text: 'Great value for money! Clean and spacious.', date: '3 days ago', helpful: 123 },
        { name: 'Emma Wilson', rating: 4, text: 'Budget hotel with good quality.', date: '1 week ago', helpful: 89 }
      ]
    },
    {
      id: 3,
      name: 'Riverside Resort, Panjim',
      location: 'Goa, India',
      country: 'India',
      area: 'Panjim',
      rating: 4.5,
      reviews: 1854,
      price: 7500,
      taxes: 1350,
      image: 'https://images.unsplash.com/photo-1610641818989-c2051b5e2cfd?auto=format&fit=crop&q=80&w=800',
      amenities: ['River View', 'Spa', 'Pool', 'Restaurant', 'Garden'],
      stars: '4',
      type: 'resort',
      fullDescription: 'Riverside Resort offers a tranquil escape overlooking the beautiful Mandovi River. This 4-star resort features lush gardens and spectacular sunset views.',
      images: [
        'https://images.unsplash.com/photo-1610641818989-c2051b5e2cfd?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&q=80&w=1200'
      ],
      comments: [
        { name: 'David Brown', rating: 5, text: 'Stunning river views! The spa treatments were excellent.', date: '5 days ago', helpful: 234 }
      ]
    },
    {
      id: 4,
      name: 'Beach Palace Hotel',
      location: 'Goa, India',
      country: 'India',
      area: 'South Goa',
      rating: 4.7,
      reviews: 3125,
      price: 15000,
      taxes: 2700,
      image: 'https://images.unsplash.com/photo-1544124499-58912cbddaad?auto=format&fit=crop&q=80&w=800',
      amenities: ['Beachfront', 'Water Sports', 'Spa', 'Pool', 'Fine Dining'],
      stars: '5',
      type: '5star',
      fullDescription: 'Beach Palace is a luxurious beachfront hotel offering world-class amenities and exceptional service in South Goa.',
      images: [
        'https://images.unsplash.com/photo-1544124499-58912cbddaad?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1512918766671-ad651ec9d273?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&q=80&w=1200'
      ],
      comments: [
        { name: 'Jennifer Lee', rating: 5, text: 'Best beach resort experience!', date: '1 week ago', helpful: 567 }
      ]
    },
    {
      id: 5,
      name: 'Dubai Burj Khalifa Hotel',
      location: 'Dubai, UAE',
      country: 'UAE',
      area: 'Downtown',
      rating: 4.9,
      reviews: 5432,
      price: 25000,
      taxes: 4500,
      image: 'https://images.unsplash.com/photo-1582672060674-bc2bd808a8b5?auto=format&fit=crop&q=80&w=800',
      amenities: ['Luxury', 'Skyscraper View', 'Spa', 'Pool', 'Michelin Restaurant'],
      stars: '5',
      type: '5star',
      fullDescription: 'Dubai Burj Khalifa Hotel offers unparalleled luxury with breathtaking views of the iconic Burj Khalifa.',
      images: [
        'https://images.unsplash.com/photo-1582672060674-bc2bd808a8b5?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1512100356956-c1c47c486959?auto=format&fit=crop&q=80&w=1200'
      ],
      comments: [
        { name: 'Ahmed Al-Maktoum', rating: 5, text: 'Outstanding luxury! Best hotel in Dubai.', date: '3 days ago', helpful: 892 }
      ]
    },
    {
      id: 6,
      name: 'Tokyo Tower Suite',
      location: 'Tokyo, Japan',
      country: 'Japan',
      area: 'Minato',
      rating: 4.7,
      reviews: 4234,
      price: 16000,
      taxes: 2880,
      image: 'https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&q=80&w=800',
      amenities: ['Luxury', 'Tower View', 'Restaurant', 'Spa', 'Business Center'],
      stars: '5',
      type: '5star',
      fullDescription: 'Tokyo Tower Suite offers stunning panoramic views of Tokyo from its elevated location in Minato.',
      images: [
        'https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1542051841857-5f90071e7989?auto=format&fit=crop&q=80&w=1200'
      ],
      comments: [
        { name: 'Hiroshi Tanaka', rating: 5, text: 'Beautiful views of Tokyo! Authentic Japanese hospitality.', date: '4 days ago', helpful: 543 }
      ]
    },
    {
      id: 7,
      name: 'Paris Eiffel Tower Hotel',
      location: 'Paris, France',
      country: 'France',
      area: '7th Arrondissement',
      rating: 4.7,
      reviews: 4234,
      price: 24000,
      taxes: 4320,
      image: 'https://images.unsplash.com/photo-1499856871958-5b9627545d1a?auto=format&fit=crop&q=80&w=800',
      amenities: ['Tower View', 'Luxury', 'Restaurant', 'Wine Cellar', 'Spa'],
      stars: '5',
      type: '5star',
      fullDescription: 'Paris Eiffel Tower Hotel is a 5-star luxury property with direct views of the iconic Eiffel Tower.',
      images: [
        'https://images.unsplash.com/photo-1499856871958-5b9627545d1a?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&q=80&w=1200'
      ],
      comments: [
        { name: 'Marie Leclerc', rating: 5, text: 'Parfait! Direct Eiffel Tower view.', date: '3 days ago', helpful: 678 }
      ]
    },
    {
      id: 8,
      name: 'Sydney Opera House Hotel',
      location: 'Sydney, Australia',
      country: 'Australia',
      area: 'CBD',
      rating: 4.8,
      reviews: 4876,
      price: 18000,
      taxes: 3240,
      image: 'https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?auto=format&fit=crop&q=80&w=800',
      amenities: ['Opera View', 'Luxury', 'Restaurant', 'Pool', 'Spa'],
      stars: '5',
      type: '5star',
      fullDescription: 'Sydney Opera House Hotel offers iconic views of the world-famous Opera House and Harbor Bridge.',
      images: [
        'https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1521899387619-c73957265207?auto=format&fit=crop&q=80&w=1200',
        'https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?auto=format&fit=crop&q=80&w=1200'
      ],
      comments: [
        { name: 'Lucas Anderson', rating: 5, text: 'Spectacular Opera House views!', date: '2 days ago', helpful: 723 }
      ]
    },
  ];

  const filteredHotels = useMemo(() => {
    let result = [...allHotels];

    if (destination.trim()) {
      const searchTerm = destination.toLowerCase();
      result = result.filter(hotel =>
        hotel.location.toLowerCase().includes(searchTerm) ||
        hotel.area.toLowerCase().includes(searchTerm) ||
        hotel.name.toLowerCase().includes(searchTerm) ||
        hotel.country.toLowerCase().includes(searchTerm)
      );
    }

    if (searchLocality.trim()) {
      const searchTerm = searchLocality.toLowerCase();
      result = result.filter(hotel =>
        hotel.area.toLowerCase().includes(searchTerm) ||
        hotel.location.toLowerCase().includes(searchTerm) ||
        hotel.country.toLowerCase().includes(searchTerm)
      );
    }

    if (selectedFilters.luxury) {
      result = result.filter(h => h.stars === '5');
    }
    if (selectedFilters.budget) {
      result = result.filter(h => h.type === 'budget');
    }
    if (selectedFilters.resort) {
      result = result.filter(h => h.type === 'resort');
    }

    result = result.filter(h => 
      h.price >= priceRange.min && h.price <= priceRange.max
    );

    if (selectedSort === 'priceLow') {
      result.sort((a, b) => a.price - b.price);
    } else if (selectedSort === 'priceHigh') {
      result.sort((a, b) => b.price - a.price);
    } else if (selectedSort === 'rating') {
      result.sort((a, b) => b.rating - a.rating);
    } else {
      result.sort((a, b) => b.reviews - a.reviews);
    }

    return result;
  }, [destination, searchLocality, selectedFilters, priceRange, selectedSort]);

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

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f8fafc' }}>
      {/* Header */}
      <header style={{
        background: 'linear-gradient(135deg, #ffffff 0%, #f8f9fb 100%)',
        borderBottom: '1px solid #e8ecf1',
        padding: '1rem',
        boxShadow: '0 2px 8px rgba(0,0,0,0.05)',
      }}>
        {/* Logo and Title Section */}
        <div style={{
          maxWidth: '1400px',
          margin: '0 auto',
          paddingBottom: '1.5rem',
          borderBottom: '1px solid #e8ecf1',
          marginBottom: '1.5rem'
        }}>
          <div style={{ 
            display: 'flex',
            alignItems: 'center',
            gap: '12px',
            fontSize: 'clamp(18px, 5vw, 28px)',
            fontWeight: 'bold',
            color: '#003580'
          }}>
            <div style={{
              width: 'clamp(48px, 10vw, 62px)',
              height: 'clamp(48px, 10vw, 62px)',
              backgroundColor: '#003580',
              borderRadius: '8px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'white',
              fontWeight: 'bold',
              fontSize: 'clamp(24px, 6vw, 32px)',
              flexShrink: 0
            }}>
               <img 
                  src={xyzs}
                  alt="Flight Booking Logo"
                  style={{ 
                    width: '72px',
                    height: '72px',
                    objectFit: 'contain'
                  }}
                />
            </div>
            <span>Hotel Booking</span>
          </div>
        </div>

        {/* Search Form Section */}
        <div style={{
          maxWidth: '1400px',
          margin: '0 auto'
        }}>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))',
            gap: '1rem',
            alignItems: 'end'
          }}>
            {/* Destination */}
            <div>
              <label style={{
                display: 'block',
                fontSize: 'clamp(0.65rem, 2vw, 0.75rem)',
                fontWeight: 700,
                color: '#64748b',
                marginBottom: '0.4rem',
                textTransform: 'uppercase',
                whiteSpace: 'nowrap'
              }}>
                City or Country
              </label>
              <div style={{ position: 'relative' }}>
                <MapPin style={{
                  position: 'absolute',
                  left: '0.75rem',
                  top: '0.65rem',
                  width: '1rem',
                  height: '1rem',
                  color: '#94a3b8',
                  pointerEvents: 'none'
                }} />
                <input
                  type="text"
                  value={destination}
                  onChange={(e) => setDestination(e.target.value)}
                  placeholder="Goa, Paris..."
                  style={{
                    width: '100%',
                    paddingLeft: '2.5rem',
                    paddingRight: '0.75rem',
                    paddingTop: '0.6rem',
                    paddingBottom: '0.6rem',
                    border: '1px solid #cbd5e1',
                    borderRadius: '0.375rem',
                    fontSize: 'clamp(0.8rem, 2vw, 0.875rem)',
                    outline: 'none',
                    transition: 'all 0.2s',
                    boxSizing: 'border-box',
                    fontFamily: 'inherit'
                  }}
                  onFocus={(e) => {
                    e.target.style.borderColor = '#3b82f6';
                    e.target.style.boxShadow = '0 0 0 2px rgba(59, 130, 246, 0.05)';
                  }}
                  onBlur={(e) => {
                    e.target.style.borderColor = '#cbd5e1';
                    e.target.style.boxShadow = 'none';
                  }}
                />
              </div>
            </div>

            {/* Check-in */}
            <div>
              <label style={{
                display: 'block',
                fontSize: 'clamp(0.65rem, 2vw, 0.75rem)',
                fontWeight: 700,
                color: '#64748b',
                marginBottom: '0.4rem',
                textTransform: 'uppercase',
                whiteSpace: 'nowrap'
              }}>
                Check In
              </label>
              <div style={{ position: 'relative' }}>
                <Calendar style={{
                  position: 'absolute',
                  left: '0.75rem',
                  top: '0.65rem',
                  width: '1rem',
                  height: '1rem',
                  color: '#94a3b8',
                  pointerEvents: 'none'
                }} />
                <input
                  type="text"
                  value={checkIn}
                  onChange={(e) => setCheckIn(e.target.value)}
                  placeholder="MM/DD/YYYY"
                  style={{
                    width: '100%',
                    paddingLeft: '2.5rem',
                    paddingRight: '0.75rem',
                    paddingTop: '0.6rem',
                    paddingBottom: '0.6rem',
                    border: '1px solid #cbd5e1',
                    borderRadius: '0.375rem',
                    fontSize: 'clamp(0.8rem, 2vw, 0.875rem)',
                    outline: 'none',
                    transition: 'all 0.2s',
                    boxSizing: 'border-box',
                    fontFamily: 'inherit'
                  }}
                  onFocus={(e) => {
                    e.target.style.borderColor = '#3b82f6';
                    e.target.style.boxShadow = '0 0 0 2px rgba(59, 130, 246, 0.05)';
                  }}
                  onBlur={(e) => {
                    e.target.style.borderColor = '#cbd5e1';
                    e.target.style.boxShadow = 'none';
                  }}
                />
              </div>
            </div>

            {/* Check-out */}
            <div>
              <label style={{
                display: 'block',
                fontSize: 'clamp(0.65rem, 2vw, 0.75rem)',
                fontWeight: 700,
                color: '#64748b',
                marginBottom: '0.4rem',
                textTransform: 'uppercase',
                whiteSpace: 'nowrap'
              }}>
                Check Out
              </label>
              <div style={{ position: 'relative' }}>
                <Calendar style={{
                  position: 'absolute',
                  left: '0.75rem',
                  top: '0.65rem',
                  width: '1rem',
                  height: '1rem',
                  color: '#94a3b8',
                  pointerEvents: 'none'
                }} />
                <input
                  type="text"
                  value={checkOut}
                  onChange={(e) => setCheckOut(e.target.value)}
                  placeholder="MM/DD/YYYY"
                  style={{
                    width: '100%',
                    paddingLeft: '2.5rem',
                    paddingRight: '0.75rem',
                    paddingTop: '0.6rem',
                    paddingBottom: '0.6rem',
                    border: '1px solid #cbd5e1',
                    borderRadius: '0.375rem',
                    fontSize: 'clamp(0.8rem, 2vw, 0.875rem)',
                    outline: 'none',
                    transition: 'all 0.2s',
                    boxSizing: 'border-box',
                    fontFamily: 'inherit'
                  }}
                  onFocus={(e) => {
                    e.target.style.borderColor = '#3b82f6';
                    e.target.style.boxShadow = '0 0 0 2px rgba(59, 130, 246, 0.05)';
                  }}
                  onBlur={(e) => {
                    e.target.style.borderColor = '#cbd5e1';
                    e.target.style.boxShadow = 'none';
                  }}
                />
              </div>
            </div>

            {/* Rooms */}
            <div>
              <label style={{
                display: 'block',
                fontSize: 'clamp(0.65rem, 2vw, 0.75rem)',
                fontWeight: 700,
                color: '#64748b',
                marginBottom: '0.4rem',
                textTransform: 'uppercase',
                whiteSpace: 'nowrap'
              }}>
                Rooms
              </label>
              <div style={{ position: 'relative' }}>
                <Users style={{
                  position: 'absolute',
                  left: '0.75rem',
                  top: '0.65rem',
                  width: '1rem',
                  height: '1rem',
                  color: '#94a3b8',
                  pointerEvents: 'none'
                }} />
                <select
                  value={rooms}
                  onChange={(e) => setRooms(e.target.value)}
                  style={{
                    width: '100%',
                    paddingLeft: '2.5rem',
                    paddingRight: '0.75rem',
                    paddingTop: '0.6rem',
                    paddingBottom: '0.6rem',
                    border: '1px solid #cbd5e1',
                    borderRadius: '0.375rem',
                    fontSize: 'clamp(0.8rem, 2vw, 0.875rem)',
                    outline: 'none',
                    backgroundColor: 'white',
                    cursor: 'pointer',
                    boxSizing: 'border-box',
                    fontFamily: 'inherit'
                  }}
                >
                  <option>1</option>
                  <option>2</option>
                  <option>3</option>
                </select>
              </div>
            </div>

            {/* Search Button */}
            <button
              style={{
                backgroundColor: '#3b82f6',
                color: 'white',
                fontWeight: 600,
                padding: '0.6rem 1.5rem',
                borderRadius: '0.375rem',
                border: 'none',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '0.5rem',
                transition: 'all 0.2s',
                fontSize: 'clamp(0.75rem, 2vw, 0.875rem)',
                height: 'fit-content',
                whiteSpace: 'nowrap',
                fontFamily: 'inherit'
              }}
              onMouseEnter={(e) => {
                e.target.style.backgroundColor = '#2563eb';
              }}
              onMouseLeave={(e) => {
                e.target.style.backgroundColor = '#3b82f6';
              }}
            >
              <Search size={18} />
              SEARCH
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main style={{ maxWidth: '1400px', margin: '0 auto', padding: 'clamp(1rem, 5vw, 2rem) 1.5rem' }}>
        {/* Title */}
        <div style={{ marginBottom: '2.5rem' }}>
          <h1 style={{ fontSize: 'clamp(1.5rem, 5vw, 2rem)', fontWeight: 700, color: '#0f172a', marginBottom: '0.5rem' }}>
            {filteredHotels.length} Hotels Found
          </h1>
          <p style={{ color: '#64748b', fontSize: 'clamp(0.9rem, 2vw, 1rem)' }}>
            {destination ? `Showing results for "${destination}"` : 'Click on any hotel to see details and reviews'}
          </p>
        </div>

        {/* Content Grid - Responsive */}
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: 'minmax(200px, 250px) 1fr',
          gap: '2rem',
          '@media (max-width: 768px)': {
            gridTemplateColumns: '1fr'
          }
        }}>
          {/* Sidebar */}
          <aside style={{ 
            '@media (max-width: 768px)': {
              display: 'grid',
              gridTemplateColumns: '1fr 1fr',
              gap: '1rem'
            }
          }}>
            {/* Locality Search */}
            <div style={{ marginBottom: '2rem' }}>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, color: '#64748b', marginBottom: '0.5rem', textTransform: 'uppercase' }}>
                Search Area
              </label>
              <div style={{ position: 'relative' }}>
                <MapPinIcon style={{
                  position: 'absolute',
                  left: '0.75rem',
                  top: '0.65rem',
                  width: '1rem',
                  height: '1rem',
                  color: '#94a3b8',
                  pointerEvents: 'none'
                }} />
                <input
                  type="text"
                  placeholder="Area, City, Country..."
                  value={searchLocality}
                  onChange={(e) => setSearchLocality(e.target.value)}
                  style={{
                    width: '100%',
                    paddingLeft: '2.5rem',
                    paddingRight: '0.75rem',
                    paddingTop: '0.6rem',
                    paddingBottom: '0.6rem',
                    border: '1px solid #cbd5e1',
                    borderRadius: '0.375rem',
                    fontSize: '0.875rem',
                    boxSizing: 'border-box'
                  }}
                />
              </div>
            </div>

            {/* Filters */}
            <div style={{ marginBottom: '2rem' }}>
              <h3 style={{ fontSize: '0.95rem', fontWeight: 700, color: '#0f172a', marginBottom: '1rem', textTransform: 'uppercase' }}>
                Hotel Type
              </h3>
              {[
                { id: 'luxury', label: 'Luxury 5 Star' },
                { id: 'resort', label: 'Resort' },
                { id: 'budget', label: 'Budget' }
              ].map(filter => (
                <label key={filter.id} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', cursor: 'pointer', marginBottom: '0.75rem' }}>
                  <input
                    type="checkbox"
                    checked={selectedFilters[filter.id] || false}
                    onChange={() => toggleFilter(filter.id)}
                    style={{ width: '1.1rem', height: '1.1rem', cursor: 'pointer', accentColor: '#3b82f6' }}
                  />
                  <span style={{ fontSize: '0.875rem', color: '#1e293b' }}>
                    {filter.label}
                  </span>
                </label>
              ))}
            </div>

            {/* Price Range */}
            <div style={{ gridColumn: '1 / -1' }}>
              <h3 style={{ fontSize: '0.95rem', fontWeight: 700, color: '#0f172a', marginBottom: '1rem', textTransform: 'uppercase' }}>
                Price Range
              </h3>
              {[
                { id: 'p1', label: 'Budget (₹0-5K)', min: 0, max: 5000 },
                { id: 'p2', label: 'Mid-Range (₹5K-15K)', min: 5000, max: 15000 },
                { id: 'p3', label: 'Luxury (₹15K+)', min: 15000, max: 100000 }
              ].map(p => (
                <label key={p.id} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', cursor: 'pointer', marginBottom: '0.75rem' }}>
                  <input
                    type="radio"
                    name="price"
                    checked={priceRange.min === p.min && priceRange.max === p.max}
                    onChange={() => setPriceRange({ min: p.min, max: p.max })}
                    style={{ width: '1.1rem', height: '1.1rem', cursor: 'pointer', accentColor: '#3b82f6' }}
                  />
                  <span style={{ fontSize: '0.875rem', color: '#1e293b' }}>
                    {p.label}
                  </span>
                </label>
              ))}
            </div>
          </aside>

          {/* Hotel List */}
          <section>
            {/* Sort Buttons */}
            <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', overflowX: 'auto', paddingBottom: '0.5rem' }}>
              {[
                { id: 'popularity', label: 'Popular' },
                { id: 'priceLow', label: 'Price ↑' },
                { id: 'priceHigh', label: 'Price ↓' },
                { id: 'rating', label: 'Rating' }
              ].map(opt => (
                <button
                  key={opt.id}
                  onClick={() => setSelectedSort(opt.id)}
                  style={{
                    padding: '0.5rem 1rem',
                    borderRadius: '2rem',
                    whiteSpace: 'nowrap',
                    fontSize: '0.875rem',
                    fontWeight: 600,
                    border: 'none',
                    cursor: 'pointer',
                    backgroundColor: selectedSort === opt.id ? '#3b82f6' : '#e2e8f0',
                    color: selectedSort === opt.id ? 'white' : '#475569',
                    transition: 'all 0.2s'
                  }}
                >
                  {opt.label}
                </button>
              ))}
            </div>

            {/* No Results */}
            {filteredHotels.length === 0 ? (
              <div style={{
                backgroundColor: 'white',
                borderRadius: '0.5rem',
                padding: '3rem 1.5rem',
                textAlign: 'center',
                border: '1px solid #e2e8f0'
              }}>
                <Search size={48} style={{ margin: '0 auto 1rem', color: '#cbd5e1' }} />
                <h3 style={{ fontSize: '1.25rem', fontWeight: 600, color: '#0f172a' }}>
                  No hotels found
                </h3>
                <p style={{ color: '#64748b', marginTop: '0.5rem' }}>
                  Try different search terms or adjust filters
                </p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {filteredHotels.map(hotel => (
                  <div
                    key={hotel.id}
                    onClick={() => setSelectedHotel(hotel)}
                    style={{
                      display: 'grid',
                      gridTemplateColumns: 'minmax(150px, 220px) 1fr 150px',
                      backgroundColor: 'white',
                      borderRadius: '0.5rem',
                      overflow: 'hidden',
                      border: '1px solid #e2e8f0',
                      transition: 'all 0.2s',
                      cursor: 'pointer'
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.boxShadow = '0 8px 16px rgba(0,0,0,0.12)';
                      e.currentTarget.style.transform = 'translateY(-2px)';
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.08)';
                      e.currentTarget.style.transform = 'translateY(0)';
                    }}>
                    {/* Image */}
                    <div style={{ position: 'relative', overflow: 'hidden', height: '180px' }}>
                      <img
                        src={hotel.image}
                        alt={hotel.name}
                        style={{
                          width: '100%',
                          height: '100%',
                          objectFit: 'cover',
                          transition: 'transform 0.3s'
                        }}
                        onMouseEnter={(e) => e.target.style.transform = 'scale(1.08)'}
                        onMouseLeave={(e) => e.target.style.transform = 'scale(1)'}
                      />
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          toggleFavorite(hotel.id);
                        }}
                        style={{
                          position: 'absolute',
                          top: '0.5rem',
                          right: '0.5rem',
                          backgroundColor: 'white',
                          border: 'none',
                          borderRadius: '50%',
                          width: '3rem',
                          height: '3rem',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          cursor: 'pointer',
                          boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                          transition: 'all 0.2s'
                        }}
                        onMouseEnter={(e) => e.currentTarget.style.transform = 'scale(1.1)'}
                        onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
                      >
                        <Heart
                          size={22}
                          fill={favorites[hotel.id] ? '#ef4444' : 'none'}
                          color={favorites[hotel.id] ? '#ef4444' : '#64748b'}
                          strokeWidth={2}
                        />
                      </button>
                    </div>

                    {/* Details */}
                    <div style={{ padding: '1rem', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                      <div>
                        <h3 style={{ fontSize: '0.95rem', fontWeight: 700, color: '#0f172a', marginBottom: '0.25rem', cursor: 'pointer' }}>
                          {hotel.name}
                        </h3>
                        <p style={{ fontSize: '0.8rem', color: '#64748b', display: 'flex', alignItems: 'center', gap: '0.35rem', marginBottom: '0.5rem' }}>
                          <MapPin size={14} />
                          {hotel.location}
                        </p>
                        <div style={{ display: 'flex', gap: '0.4rem', flexWrap: 'wrap' }}>
                          {hotel.amenities.slice(0, 2).map((a, i) => (
                            <span key={i} style={{
                              fontSize: '0.65rem',
                              backgroundColor: '#dbeafe',
                              color: '#1e40af',
                              padding: '0.2rem 0.4rem',
                              borderRadius: '0.2rem'
                            }}>
                              {a}
                            </span>
                          ))}
                        </div>
                      </div>
                    </div>

                    {/* Pricing */}
                    <div style={{
                      padding: '1rem',
                      backgroundColor: '#f8fafc',
                      display: 'flex',
                      flexDirection: 'column',
                      justifyContent: 'space-between',
                      borderLeft: '1px solid #e2e8f0'
                    }}>
                      <div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', marginBottom: '0.25rem' }}>
                          <Star size={14} fill="#fbbf24" color="#fbbf24" />
                          <span style={{ fontWeight: 700, color: '#0f172a', fontSize: '0.9rem' }}>
                            {hotel.rating}
                          </span>
                          <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                            ({hotel.reviews})
                          </span>
                        </div>
                        <p style={{ fontSize: '1.2rem', fontWeight: 700, color: '#0f172a' }}>
                          ₹{hotel.price.toLocaleString()}
                        </p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>
        </div>
      </main>

      {/* Modal */}
      {selectedHotel && (
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
          padding: '1rem',
          overflowY: 'auto'
        }}
          onClick={() => setSelectedHotel(null)}>
          <div
            onClick={(e) => e.stopPropagation()}
            style={{
              backgroundColor: 'white',
              borderRadius: '0.75rem',
              maxWidth: '900px',
              width: '100%',
              maxHeight: '90vh',
              overflowY: 'auto',
              position: 'relative'
            }}>
            {/* Close Button */}
            <button
              onClick={() => setSelectedHotel(null)}
              style={{
                position: 'absolute',
                top: '1rem',
                right: '1rem',
                backgroundColor: '#1f2937',
                border: '2px solid white',
                borderRadius: '50%',
                width: '3rem',
                height: '3rem',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer',
                zIndex: 10,
                boxShadow: '0 4px 12px rgba(0,0,0,0.4)',
                transition: 'all 0.2s'
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.backgroundColor = '#111827';
                e.currentTarget.style.transform = 'scale(1.1)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.backgroundColor = '#1f2937';
                e.currentTarget.style.transform = 'scale(1)';
              }}>
              <X size={26} color="white" strokeWidth={3} />
            </button>

            {/* Image Gallery */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: '2fr 1fr 1fr',
              gap: '0.5rem',
              padding: '1rem',
              backgroundColor: '#f8fafc'
            }}>
              <img
                src={selectedHotel.images[0]}
                alt="Main"
                style={{
                  width: '100%',
                  height: '300px',
                  objectFit: 'cover',
                  borderRadius: '0.5rem'
                }}
              />
              {selectedHotel.images.slice(1).map((img, i) => (
                <img
                  key={i}
                  src={img}
                  alt={`Gallery ${i}`}
                  style={{
                    width: '100%',
                    height: '300px',
                    objectFit: 'cover',
                    borderRadius: '0.5rem'
                  }}
                />
              ))}
            </div>

            {/* Content */}
            <div style={{ padding: '2rem' }}>
              {/* Header */}
              <div style={{ marginBottom: '2rem' }}>
                <h1 style={{ fontSize: 'clamp(1.5rem, 5vw, 2rem)', fontWeight: 700, color: '#0f172a', marginBottom: '0.5rem' }}>
                  {selectedHotel.name}
                </h1>
                <p style={{ fontSize: '1rem', color: '#64748b', display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
                  <MapPin size={18} />
                  {selectedHotel.location}, {selectedHotel.country}
                </p>

                {/* Rating */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1rem', flexWrap: 'wrap' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    {[...Array(5)].map((_, i) => (
                      <Star
                        key={i}
                        size={20}
                        fill={i < Math.round(selectedHotel.rating) ? '#fbbf24' : '#e5e7eb'}
                        color={i < Math.round(selectedHotel.rating) ? '#fbbf24' : '#d1d5db'}
                      />
                    ))}
                  </div>
                  <span style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0f172a' }}>
                    {selectedHotel.rating} / 5
                  </span>
                  <span style={{ fontSize: '1rem', color: '#64748b' }}>
                    ({selectedHotel.reviews.toLocaleString()} reviews)
                  </span>
                </div>
              </div>

              {/* Price & Amenities */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', marginBottom: '2rem', paddingBottom: '2rem', borderBottom: '1px solid #e2e8f0' }}>
                <div>
                  <h3 style={{ fontSize: '0.875rem', fontWeight: 700, color: '#64748b', marginBottom: '0.5rem', textTransform: 'uppercase' }}>
                    Price per Night
                  </h3>
                  <p style={{ fontSize: '2rem', fontWeight: 700, color: '#0f172a' }}>
                    ₹{selectedHotel.price.toLocaleString()}
                  </p>
                  <p style={{ fontSize: '0.875rem', color: '#64748b' }}>
                    + ₹{selectedHotel.taxes.toLocaleString()} taxes & fees
                  </p>
                </div>
                <div>
                  <h3 style={{ fontSize: '0.875rem', fontWeight: 700, color: '#64748b', marginBottom: '0.5rem', textTransform: 'uppercase' }}>
                    Amenities
                  </h3>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                    {selectedHotel.amenities.map((amenity, i) => (
                      <span key={i} style={{
                        fontSize: '0.85rem',
                        backgroundColor: '#dbeafe',
                        color: '#1e40af',
                        padding: '0.4rem 0.8rem',
                        borderRadius: '2rem',
                        fontWeight: 500
                      }}>
                        ✓ {amenity}
                      </span>
                    ))}
                  </div>
                </div>
              </div>

              {/* Description */}
              <div style={{ marginBottom: '2rem' }}>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0f172a', marginBottom: '1rem' }}>
                  About this hotel
                </h3>
                <p style={{ fontSize: '1rem', color: '#475569', lineHeight: 1.6 }}>
                  {selectedHotel.fullDescription}
                </p>
              </div>

              {/* Reviews Section */}
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem' }}>
                  <MessageSquare size={24} color="#3b82f6" />
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0f172a' }}>
                    Guest Reviews ({selectedHotel.comments.length})
                  </h3>
                </div>

                {/* Comments */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                  {selectedHotel.comments.map((comment, i) => (
                    <div key={i} style={{ borderBottom: '1px solid #e2e8f0', paddingBottom: '1.5rem' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '0.75rem' }}>
                        <div>
                          <h4 style={{ fontSize: '1rem', fontWeight: 700, color: '#0f172a' }}>
                            {comment.name}
                          </h4>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: '0.25rem', flexWrap: 'wrap' }}>
                            {[...Array(5)].map((_, j) => (
                              <Star
                                key={j}
                                size={14}
                                fill={j < comment.rating ? '#fbbf24' : '#e5e7eb'}
                                color={j < comment.rating ? '#fbbf24' : '#d1d5db'}
                              />
                            ))}
                            <span style={{ fontSize: '0.75rem', color: '#64748b' }}>
                              {comment.date}
                            </span>
                          </div>
                        </div>
                      </div>
                      <p style={{ fontSize: '0.95rem', color: '#475569', lineHeight: 1.6, marginBottom: '1rem' }}>
                        {comment.text}
                      </p>
                      <button style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.5rem',
                        fontSize: '0.85rem',
                        color: '#3b82f6',
                        background: 'none',
                        border: 'none',
                        cursor: 'pointer',
                        padding: 0
                      }}>
                        <ThumbsUp size={16} />
                        Helpful ({comment.helpful})
                      </button>
                    </div>
                  ))}
                </div>
              </div>

              {/* Book Button */}
              <div style={{ marginTop: '2rem', display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
                <button style={{
                  flex: '1 1 200px',
                  backgroundColor: '#3b82f6',
                  color: 'white',
                  border: 'none',
                  padding: '1rem',
                  borderRadius: '0.5rem',
                  fontSize: '1rem',
                  fontWeight: 700,
                  cursor: 'pointer',
                  transition: 'all 0.2s'
                }}
                  onMouseEnter={(e) => e.target.style.backgroundColor = '#2563eb'}
                  onMouseLeave={(e) => e.target.style.backgroundColor = '#3b82f6'}>
                  Book Now
                </button>
                <button style={{
                  flex: '1 1 200px',
                  backgroundColor: 'white',
                  color: '#3b82f6',
                  border: '2px solid #3b82f6',
                  padding: '1rem',
                  borderRadius: '0.5rem',
                  fontSize: '1rem',
                  fontWeight: 700,
                  cursor: 'pointer',
                  transition: 'all 0.2s'
                }}
                  onMouseEnter={(e) => e.target.style.backgroundColor = '#eff6ff'}
                  onMouseLeave={(e) => e.target.style.backgroundColor = 'white'}>
                  Add to Wishlist
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Chat Widget */}
      <div style={{ position: 'fixed', bottom: '2rem', right: '2rem', zIndex: 100 }}>
        <button style={{
          width: '3rem',
          height: '3rem',
          borderRadius: '50%',
          backgroundColor: '#3b82f6',
          color: 'white',
          border: 'none',
          fontSize: '1.25rem',
          fontWeight: 700,
          cursor: 'pointer',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          boxShadow: '0 4px 12px rgba(59, 130, 246, 0.4)',
          transition: 'all 0.2s'
        }}
          onMouseEnter={(e) => e.target.style.transform = 'scale(1.1)'}
          onMouseLeave={(e) => e.target.style.transform = 'scale(1)'}>
          ?
        </button>
      </div>
    </div>
  );
}