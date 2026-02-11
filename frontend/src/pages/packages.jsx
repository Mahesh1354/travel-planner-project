import React, { useState } from 'react';
import { 
  MapPin, Star, Calendar, Clock, Users, Search, Heart, X, 
  Filter, ChevronRight, TrendingUp, AlertCircle, CheckCircle2, XCircle
} from 'lucide-react';
import xyza from '../assets/holiday.png';

const HolidayPackagesPage = () => {
  const [destination, setDestination] = useState('');
  const [departureDate, setDepartureDate] = useState('2026-02-15');
  const [duration, setDuration] = useState('7');
  const [travelers, setTravelers] = useState('2');
  const [selectedSort, setSelectedSort] = useState('popularity');
  const [favorites, setFavorites] = useState({});
  const [selectedFilters, setSelectedFilters] = useState({});
  const [priceRange, setPriceRange] = useState({ min: 0, max: 500000 });
  const [selectedPackage, setSelectedPackage] = useState(null);

  const packages = [
    {
      id: 1,
      name: 'Goa Beach Paradise 7 Days',
      location: 'Goa, India',
      country: 'India',
      rating: 4.8,
      reviews: 1243,
      price: 34999,
      originalPrice: 45000,
      duration: '7 Days / 6 Nights',
      travelers: '2 Adults',
      category: 'Beach',
      image: 'https://r1imghtlak.mmtcdn.com/2acf5480ff6111e7b92d0202c34c106a.jpg',
      bestSeason: 'Nov - Feb',
      highlights: ['Beachfront Resort', '5-Star Hotel', 'Water Sports', 'Local Tours'],
      type: 'couple',
      fullDescription: 'Experience the magic of Goa with this all-inclusive 7-day beach getaway. Stay at a 5-star beachfront resort, enjoy water sports, explore local culture, and relax on pristine beaches.',
      itinerary: [
        { day: 1, title: 'Arrival in Goa', desc: 'Arrive at Goa Airport, transfer to 5-star beachfront resort. Evening beach walk and dinner by the sea.' },
        { day: 2, title: 'North Goa Exploration', desc: 'Visit Calangute Beach, Vagator Beach, and Fort Aguada. Explore local markets and cuisine.' },
        { day: 3, title: 'Water Sports Adventure', desc: 'Para-sailing, Jet Skiing, and Banana Boat rides at Baga Beach.' },
        { day: 4, title: 'Cultural Tour', desc: 'Visit Old Goa churches, Mangueshi Temple, and spice plantations.' },
        { day: 5, title: 'South Goa Relax', desc: 'Day trip to Palolem Beach, dolphin watching, and sunset cruise.' },
        { day: 6, title: 'Spa & Leisure', desc: 'Full day spa treatment, beach relaxation, and beachside dining.' },
        { day: 7, title: 'Departure', desc: 'Breakfast and departure to airport.' }
      ],
      inclusions: ['5-Star Hotel (6 nights)', 'All Breakfasts', 'Airport Transfers', 'Water Sports (3 activities)', 'Cultural Tour', 'Spa Treatment (1 hour)', 'Guided Beach Walk'],
      exclusions: ['Flights', 'Lunch & Dinner (except 1 welcome dinner)', 'Travel Insurance', 'Personal Activities'],
      images: [
        'https://static.toiimg.com/thumb/msid-55310626,width-748,height-499,resizemode=4,imgsize-175052/.jpg',
        'https://hblimg.mmtcdn.com/content/hubble/img/goa/mmt/activities/t_ufs/m_Vagator%20Beach-3_l_360_640.jpg',
        'https://hblimg.mmtcdn.com/content/hubble/img/your_directory_name/mmt/activities/t_ufs/m_ChIJ60HZJlpFvjsRKWwSX-EWbsQ_2_l_3599_4799.jpeg'
      ]
    },
    {
      id: 2,
      name: 'Maldives Luxury Honeymoon 5 Days',
      location: 'Malé, Maldives',
      country: 'Maldives',
      rating: 5.0,
      reviews: 892,
      price: 89999,
      originalPrice: 120000,
      duration: '5 Days / 4 Nights',
      travelers: '2 Adults',
      category: 'Luxury',
      image: 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/2e/92/29/4d/caption.jpg?w=900&h=500&s=1',
      bestSeason: 'Dec - April',
      highlights: ['Overwater Villa', 'All Meals Included', 'Spa Package', 'Sunset Cruise'],
      type: 'honeymoon',
      fullDescription: 'Perfect romantic getaway in the Maldives. Stay in a private overwater villa, enjoy world-class dining, spa treatments, and water activities in tropical paradise.',
      itinerary: [
        { day: 1, title: 'Island Paradise Arrival', desc: 'Seaplane transfer to private island resort. Welcome drinks and sunset dinner.' },
        { day: 2, title: 'Snorkeling Safari', desc: 'Guided snorkeling tour exploring coral reefs and marine life.' },
        { day: 3, title: 'Island Hopping & Spa', desc: 'Visit local islands, full spa treatment at the resort.' },
        { day: 4, title: 'Sunset Cruise', desc: 'Romantic sunset cruise with champagne and fine dining.' },
        { day: 5, title: 'Departure', desc: 'Breakfast and seaplane departure.' }
      ],
      inclusions: ['Overwater Villa (4 nights)', 'All Meals & Premium Drinks', 'Seaplane Transfers', 'Snorkeling Gear', 'Couple Spa Package (2 hours)', 'Sunset Cruise', 'Water Sports'],
      exclusions: ['International Flights', 'Visa Fees', 'Travel Insurance'],
      images: [
        'https://www.thetravelmagazine.net/wp-content/uploads/Bodu.jpg',
        'https://etripto.in/uploads/0000/1/2025/01/13/top-10-places-to-visit-in-maldives.jpg',
        'https://ychef.files.bbci.co.uk/1920x960/p0brkx26.png'
      ]
    },
    {
      id: 3,
      name: 'Himalayan Trek Adventure 10 Days',
      location: 'Himachal, India',
      country: 'India',
      rating: 4.9,
      reviews: 567,
      price: 42999,
      originalPrice: 58000,
      duration: '10 Days / 9 Nights',
      travelers: '4-6 People',
      category: 'Adventure',
      image: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSTpWIGsljGNrmg1gNVh5OAmP30Ka-999dRfA&s',
      bestSeason: 'June - Sept',
      highlights: ['Mountain Trekking', 'Expert Guides', 'Alpine Meadows', 'Local Villages'],
      type: 'adventure',
      fullDescription: 'Challenge yourself with this 10-day Himalayan trekking adventure. Trek through alpine meadows, visit remote villages, and experience stunning mountain vistas with expert guides.',
      itinerary: [
        { day: 1, title: 'Arrival at Shimla', desc: 'Drive to Shimla, briefing and equipment check.' },
        { day: 2, title: 'Trek to Jachh', desc: 'Start trek via Skandhanag Forest to Jachh (3,560m).' },
        { day: 3, title: 'Jachh to Chirgaon', desc: 'Trek through meadows to Chirgaon camping site.' },
        { day: 4, title: 'High Altitude Trek', desc: 'Trek to Koti Pass (4,110m), highest point of trek.' },
        { day: 5, title: 'Rest & Acclimatization', desc: 'Rest day with local village exploration.' },
        { day: 6, title: 'Trek to Alpine Meadows', desc: 'Beautiful trek through wildflower meadows.' },
        { day: 7, title: 'Descent Day', desc: 'Trek downhill to lower altitude camp.' },
        { day: 8, title: 'Village Visit', desc: 'Visit traditional Himachali villages, interact with locals.' },
        { day: 9, title: 'Final Trek & Descent', desc: 'Trek completion and drive back to Shimla.' },
        { day: 10, title: 'Departure', desc: 'Breakfast and departure.' }
      ],
      inclusions: ['Guest House/Tent Accommodation', 'All Meals (Trek)', 'Expert Mountain Guides', 'Trek Permits', 'Oxygen Cylinder', 'First Aid Kit', 'Porter Service'],
      exclusions: ['Flights to Himachal', 'Personal Trekking Gear', 'Travel Insurance', 'Tips & Gratuities'],
      images: [
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ4IUavhStjnQbJeqTp24IgGB3DldHxWkg5lw&s',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRRZvMB1k7DVHGJW-s4wa5QPV6bEJgDldKB-A&s',
        'https://www.trekupindia.com/wp-content/uploads/1715/38/Goechala-Trek.jpg'
      ]
    },
    {
      id: 4,
      name: 'Kerala Backwaters Family 6 Days',
      location: 'Kerala, India',
      country: 'India',
      rating: 4.7,
      reviews: 1456,
      price: 28999,
      originalPrice: 39000,
      duration: '6 Days / 5 Nights',
      travelers: '2 Adults + 2 Kids',
      category: 'Family',
      image: 'https://www.keralaholidays.com/uploads/tourpackages/main/wwww.jpg',
      bestSeason: 'Oct - March',
      highlights: ['Houseboat Cruise', 'Ayurveda Spa', 'Beach Resort', 'Local Markets'],
      type: 'family',
      fullDescription: 'Enjoy quality family time in Kerala with houseboat cruises on backwaters, ayurveda treatments, and beach relaxation. Perfect for all age groups.',
      itinerary: [
        { day: 1, title: 'Kochi Arrival', desc: 'Arrive in Kochi, check-in to beach resort.' },
        { day: 2, title: 'Fort Kochi Heritage', desc: 'Explore historic Fort Kochi, Chinese fishing nets, local spice markets.' },
        { day: 3, title: 'Houseboat Cruise', desc: 'Full day houseboat cruise on scenic backwaters.' },
        { day: 4, title: 'Beach & Ayurveda', desc: 'Beach activities and ayurveda spa treatments.' },
        { day: 5, title: 'Leisure & Shopping', desc: 'Beach leisure day, souvenir shopping.' },
        { day: 6, title: 'Departure', desc: 'Breakfast and departure.' }
      ],
      inclusions: ['4-Star Beach Resort (5 nights)', 'All Breakfasts', 'Houseboat Cruise (Full Day)', 'Ayurveda Massage (Family)', 'Airport Transfers', 'Local Guide', 'Water Sports'],
      exclusions: ['Flights', 'Meals (except breakfast)', 'Travel Insurance', 'Activities beyond itinerary'],
      images: [
        'https://cdn.jacadatravel.com/wp-content/uploads/bis-images/151094/location__BackwaterBoatWF-2400x1400-f50_50.jpg',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQiR550okeD9TGaregB5Dqw05dcxO3lIQK_NA&s',
        'https://cdn.i-scmp.com/sites/default/files/styles/1020x680/public/images/methode/2017/10/25/df4d59ec-b7af-11e7-affb-32c8d8b6484e_1280x720_173153.JPG?itok=RhHC6ZEI'
      ]
    },
    {
      id: 5,
      name: 'Rajasthan Royal Tour 8 Days',
      location: 'Jaipur-Udaipur, India',
      country: 'India',
      rating: 4.6,
      reviews: 2103,
      price: 39999,
      originalPrice: 52000,
      duration: '8 Days / 7 Nights',
      travelers: '2-4 People',
      category: 'Cultural',
      image: 'https://empiretourstravels.com/wp-content/uploads/2025/05/royal-raj.png',
      bestSeason: 'Oct - March',
      highlights: ['Palace Tours', 'City Palace', 'Lake City', 'Local Culture'],
      type: 'cultural',
      fullDescription: 'Explore the royal heritage of Rajasthan. Visit magnificent palaces, forts, temples, and experience the vibrant culture of the Pink City and Lake City.',
      itinerary: [
        { day: 1, title: 'Jaipur Arrival', desc: 'Arrive in Jaipur, check-in to heritage hotel.' },
        { day: 2, title: 'Jaipur City Tour', desc: 'Visit City Palace, Hawa Mahal, Jantar Mantar.' },
        { day: 3, title: 'Amber Fort', desc: 'Full day tour of magnificent Amber Fort.' },
        { day: 4, title: 'Shopping & Culture', desc: 'Local bazaar shopping, traditional handicrafts.' },
        { day: 5, title: 'Drive to Udaipur', desc: '5-hour scenic drive to Lake City Udaipur.' },
        { day: 6, title: 'City Palace & Lakes', desc: 'Visit City Palace, Lake Pichola cruise, sunset ride.' },
        { day: 7, title: 'Temple & Bazaar', desc: 'Visit Jagdish Temple, shopping at local markets.' },
        { day: 8, title: 'Departure', desc: 'Breakfast and departure.' }
      ],
      inclusions: ['Heritage Hotel Stay (7 nights)', 'All Breakfasts', 'City Tours', 'Fort Entry Fees', 'Lake Cruise', 'Airport Transfers', 'Local Guide'],
      exclusions: ['Flights', 'Lunch & Dinner', 'Travel Insurance', 'Personal Activities'],
      images: [
        'https://privatedriverwithcar.com/assets/images/package/r-5.jpg',
        'https://www.travelosei.com/wp-content/uploads/2025/05/500793-C-cultural-royal-tour-of-jaisalmer-fort.webp',
        'https://www.rajasthantrip.com/storage/images/blogs/royal%20hotel.jpg'
      ]
    },
    {
      id: 6,
      name: 'Bali Island Escape 6 Days',
      location: 'Bali, Indonesia',
      country: 'Indonesia',
      rating: 4.9,
      reviews: 1876,
      price: 54999,
      originalPrice: 72000,
      duration: '6 Days / 5 Nights',
      travelers: '2 Adults',
      category: 'Beach',
      image: 'https://ssvtours.in/wp-content/uploads/2022/03/bali-4-min-1024x567.png',
      bestSeason: 'April - Oct',
      highlights: ['Luxury Resort', 'Temple Tours', 'Beach Activities', 'Spa & Wellness'],
      type: 'couple',
      fullDescription: 'Experience tropical paradise in Bali. Luxury beachfront resort, ancient temples, traditional spa treatments, and stunning sunsets await you.',
      itinerary: [
        { day: 1, title: 'Bali Arrival', desc: 'Arrive in Bali, transfer to luxury beach resort.' },
        { day: 2, title: 'Temple Tours', desc: 'Visit Tanah Lot, Uluwatu, and Tirta Empul temples.' },
        { day: 3, title: 'Beach & Water Sports', desc: 'Surfing, parasailing, and beach relaxation.' },
        { day: 4, title: 'Spa Day', desc: 'Full spa treatment and wellness activities.' },
        { day: 5, title: 'Village Experience', desc: 'Visit traditional Balinese villages and rice paddies.' },
        { day: 6, title: 'Departure', desc: 'Breakfast and departure.' }
      ],
      inclusions: ['5-Star Beach Resort (5 nights)', 'All Meals & Drinks', 'Airport Transfers', 'Temple Tours', 'Spa Package (2 hours)', 'Water Sports', 'Activities'],
      exclusions: ['International Flights', 'Visa Fees', 'Travel Insurance'],
      images: [
        'https://pix10.agoda.net/hotelImages/113/1134509/1134509_15111011080037630854.jpg?ca=&ce=1&s=414x232',
        'https://www.national-park.com/wp-content/uploads/2024/09/Exploring-Bali-A-Tropical-Escape-Like-No-Other.jpg',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQps5KHDCrJmReWK9oX94yy60T8bV3z0eJUWw&s'
      ]
    }
  ];

  const filteredPackages = packages.filter(pkg => {
    if (destination.trim()) {
      const searchTerm = destination.toLowerCase();
      if (!pkg.location.toLowerCase().includes(searchTerm) && 
          !pkg.country.toLowerCase().includes(searchTerm) &&
          !pkg.name.toLowerCase().includes(searchTerm)) {
        return false;
      }
    }

    if (selectedFilters.luxury && pkg.category !== 'Luxury') return false;
    if (selectedFilters.budget && pkg.price > 50000) return false;
    if (selectedFilters.adventure && pkg.category !== 'Adventure') return false;
    if (selectedFilters.family && pkg.type !== 'family') return false;

    if (pkg.price < priceRange.min || pkg.price > priceRange.max) return false;

    return true;
  });

  const sortedPackages = [...filteredPackages].sort((a, b) => {
    if (selectedSort === 'price') return a.price - b.price;
    if (selectedSort === 'rating') return b.rating - a.rating;
    return b.reviews - a.reviews;
  });

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

  const discountPercent = (pkg) => {
    return Math.round(((pkg.originalPrice - pkg.price) / pkg.originalPrice) * 100);
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
                fontSize: '24px'
              }}>
                <img 
                    src={xyza}
                    alt="Flight Booking Logo"
                    style={{ 
                      width: '62px',
                      height: '62px',
                      objectFit: 'contain'
                    }}
                  />
              </div>
              Holiday Packages
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
            {/* Destination */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>DESTINATION</label>
              <input
                type="text"
                value={destination}
                onChange={(e) => setDestination(e.target.value)}
                placeholder="Where to go?"
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

            {/* Departure Date */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>DEPARTURE</label>
              <input
                type="date"
                value={departureDate}
                onChange={(e) => setDepartureDate(e.target.value)}
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

            {/* Duration */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>DURATION</label>
              <select
                value={duration}
                onChange={(e) => setDuration(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              >
                <option value="3">3 Days</option>
                <option value="5">5 Days</option>
                <option value="7">7 Days</option>
                <option value="10">10 Days</option>
                <option value="14">14 Days</option>
              </select>
            </div>

            {/* Travelers */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>TRAVELERS</label>
              <select
                value={travelers}
                onChange={(e) => setTravelers(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              >
                <option value="1">1 Person</option>
                <option value="2">2 People</option>
                <option value="4">4 People</option>
                <option value="6">6 People</option>
              </select>
            </div>

            {/* Budget Range */}
            <div>
              <label style={{ fontSize: '12px', color: '#666', fontWeight: '500', display: 'block', marginBottom: '5px' }}>BUDGET</label>
              <select
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '14px',
                  boxSizing: 'border-box',
                }}
              >
                <option>All Budgets</option>
                <option>₹0 - ₹30K</option>
                <option>₹30K - ₹50K</option>
                <option>₹50K - ₹100K</option>
                <option>₹100K+</option>
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
      <div style={{ maxWidth: '1400px', margin: '0 auto', padding: '30px 20px', display: 'grid', gridTemplateColumns: '280px 1fr', gap: '30px' }}>
        {/* Sidebar Filters */}
        <aside style={{ background: 'white', padding: '20px', borderRadius: '8px', height: 'fit-content', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
          <h3 style={{ fontSize: '16px', fontWeight: '600', marginBottom: '20px' }}>Filters</h3>

          {/* Package Type */}
          <div style={{ marginBottom: '25px' }}>
            <h4 style={{ fontSize: '13px', fontWeight: '600', color: '#666', marginBottom: '12px', textTransform: 'uppercase' }}>Package Type</h4>
            {[
              { id: 'luxury', label: 'Luxury' },
              { id: 'budget', label: 'Budget Friendly' },
              { id: 'adventure', label: 'Adventure' },
              { id: 'family', label: 'Family' }
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
            <h4 style={{ fontSize: '13px', fontWeight: '600', color: '#666', marginBottom: '12px', textTransform: 'uppercase' }}>Price Range</h4>
            <div style={{ marginBottom: '12px' }}>
              <input 
                type="range" 
                min="0" 
                max="500000" 
                step="10000"
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

          {/* Duration */}
          <div style={{ paddingTop: '20px', borderTop: '1px solid #eee' }}>
            <h4 style={{ fontSize: '13px', fontWeight: '600', color: '#666', marginBottom: '12px', textTransform: 'uppercase' }}>Duration</h4>
            {['3-5 Days', '5-7 Days', '7-10 Days', '10+ Days'].map((d, i) => (
              <label key={i} style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', marginBottom: '10px', fontSize: '13px' }}>
                <input type="checkbox" style={{ cursor: 'pointer' }} />
                {d}
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
                {sortedPackages.length} Packages Found
              </h2>
              <p style={{ color: '#666', fontSize: '13px' }}>
                {destination ? `Showing results for "${destination}"` : 'Explore our handpicked holiday packages'}
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
                <option value="rating">Rating</option>
              </select>
            </div>
          </div>

          {/* Packages Grid */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '20px' }}>
            {sortedPackages.map(pkg => (
              <div
                key={pkg.id}
                onClick={() => setSelectedPackage(pkg)}
                style={{
                  background: 'white',
                  borderRadius: '8px',
                  overflow: 'hidden',
                  border: '1px solid #e8ecf1',
                  transition: 'all 0.3s',
                  cursor: 'pointer',
                  boxShadow: '0 2px 4px rgba(0,0,0,0.05)'
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.boxShadow = '0 8px 16px rgba(0,0,0,0.12)';
                  e.currentTarget.style.transform = 'translateY(-4px)';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.boxShadow = '0 2px 4px rgba(0,0,0,0.05)';
                  e.currentTarget.style.transform = 'translateY(0)';
                }}>
                
                {/* Image */}
                <div style={{ position: 'relative', height: '220px', overflow: 'hidden' }}>
                  <img
                    src={pkg.image}
                    alt={pkg.name}
                    style={{
                      width: '100%',
                      height: '100%',
                      objectFit: 'cover',
                      transition: 'transform 0.5s'
                    }}
                    onMouseEnter={(e) => e.target.style.transform = 'scale(1.1)'}
                    onMouseLeave={(e) => e.target.style.transform = 'scale(1)'}
                  />
                  
                  {/* Discount Badge */}
                  {pkg.originalPrice && (
                    <div style={{
                      position: 'absolute',
                      top: '10px',
                      right: '10px',
                      background: '#ff4757',
                      color: 'white',
                      padding: '6px 12px',
                      borderRadius: '4px',
                      fontSize: '12px',
                      fontWeight: '600'
                    }}>
                      -{discountPercent(pkg)}%
                    </div>
                  )}

                  {/* Category Badge */}
                  <div style={{
                    position: 'absolute',
                    top: '10px',
                    left: '10px',
                    background: 'rgba(255,255,255,0.95)',
                    padding: '5px 10px',
                    borderRadius: '4px',
                    fontSize: '11px',
                    fontWeight: '600',
                    color: '#003580'
                  }}>
                    {pkg.category}
                  </div>

                  {/* Favorite Button - IMPROVED */}
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      toggleFavorite(pkg.id);
                    }}
                    style={{
                      position: 'absolute',
                      bottom: '15px',
                      right: '15px',
                      width: '55px',
                      height: '55px',
                      background: 'white',
                      border: '2px solid #ff4757',
                      borderRadius: '50%',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      cursor: 'pointer',
                      boxShadow: '0 4px 16px rgba(0,0,0,0.15)',
                      transition: 'all 0.3s ease',
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.transform = 'scale(1.15)';
                      e.currentTarget.style.boxShadow = '0 8px 20px rgba(255, 71, 87, 0.4)';
                      e.currentTarget.style.background = favorites[pkg.id] ? '#ff4757' : '#fff5f7';
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.transform = 'scale(1)';
                      e.currentTarget.style.boxShadow = '0 4px 16px rgba(0,0,0,0.15)';
                      e.currentTarget.style.background = 'white';
                    }}
                  >
                    <Heart
                      size={32}
                      fill={favorites[pkg.id] ? '#ff4757' : 'none'}
                      color={favorites[pkg.id] ? '#ff4757' : '#ff4757'}
                      strokeWidth={1.5}
                    />
                  </button>
                </div>

                {/* Content */}
                <div style={{ padding: '15px' }}>
                  <h3 style={{ fontSize: '15px', fontWeight: '700', color: '#0f172a', marginBottom: '8px' }}>
                    {pkg.name}
                  </h3>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '4px', marginBottom: '10px', fontSize: '12px', color: '#666' }}>
                    <MapPin size={14} />
                    {pkg.location}
                  </div>

                  <div style={{ display: 'flex', gap: '8px', marginBottom: '12px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '3px', fontSize: '11px', background: '#f5f5f5', padding: '4px 8px', borderRadius: '3px', color: '#666' }}>
                      <Clock size={12} />
                      {pkg.duration.split(' / ')[0]}
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '3px', fontSize: '11px', background: '#f5f5f5', padding: '4px 8px', borderRadius: '3px', color: '#666' }}>
                      <Users size={12} />
                      {pkg.travelers}
                    </div>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '12px' }}>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          size={12}
                          fill={i < Math.floor(pkg.rating) ? '#fbbf24' : '#e5e7eb'}
                          color={i < Math.floor(pkg.rating) ? '#fbbf24' : '#d1d5db'}
                        />
                      ))}
                    </div>
                    <span style={{ fontSize: '12px', color: '#666' }}>
                      {pkg.rating} ({pkg.reviews} reviews)
                    </span>
                  </div>

                  {/* Price */}
                  <div style={{ borderTop: '1px solid #eee', paddingTop: '12px' }}>
                    <div style={{ display: 'flex', alignItems: 'baseline', gap: '6px', marginBottom: '10px' }}>
                      {pkg.originalPrice && (
                        <span style={{ fontSize: '12px', color: '#999', textDecoration: 'line-through' }}>
                          ₹{pkg.originalPrice.toLocaleString()}
                        </span>
                      )}
                      <span style={{ fontSize: '18px', fontWeight: '700', color: '#003580' }}>
                        ₹{pkg.price.toLocaleString()}
                      </span>
                    </div>
                    <p style={{ fontSize: '10px', color: '#666' }}>per person</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </main>
      </div>

      {/* Modal */}
      {selectedPackage && (
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
        onClick={() => setSelectedPackage(null)}>
          <div
            onClick={(e) => e.stopPropagation()}
            style={{
              backgroundColor: 'white',
              borderRadius: '8px',
              maxWidth: '900px',
              width: '100%',
              maxHeight: '90vh',
              overflowY: 'auto',
              position: 'relative'
            }}>
            
            {/* Close Button */}
            <button
              onClick={() => setSelectedPackage(null)}
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
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
              }}>
              <X size={20} />
            </button>

            {/* Image Gallery */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(3, 1fr)',
              gap: '10px',
              padding: '15px',
              backgroundColor: '#f5f7fa'
            }}>
              {selectedPackage.images.map((img, i) => (
                <img
                  key={i}
                  src={img}
                  alt={`Gallery ${i}`}
                  style={{
                    width: '100%',
                    height: '200px',
                    objectFit: 'cover',
                    borderRadius: '4px'
                  }}
                />
              ))}
            </div>

            {/* Content */}
            <div style={{ padding: '25px' }}>
              {/* Header */}
              <h2 style={{ fontSize: '28px', fontWeight: '700', color: '#0f172a', marginBottom: '10px' }}>
                {selectedPackage.name}
              </h2>

              <div style={{ display: 'flex', gap: '10px', alignItems: 'center', marginBottom: '20px', flexWrap: 'wrap' }}>
                <span style={{ fontSize: '13px', background: '#003580', color: 'white', padding: '6px 12px', borderRadius: '4px', fontWeight: '600' }}>
                  {selectedPackage.duration}
                </span>
                <span style={{ fontSize: '13px', background: '#f0f0f0', color: '#666', padding: '6px 12px', borderRadius: '4px', fontWeight: '500' }}>
                  {selectedPackage.bestSeason}
                </span>
                <span style={{ fontSize: '13px', background: '#f0f0f0', color: '#666', padding: '6px 12px', borderRadius: '4px', fontWeight: '500' }}>
                  {selectedPackage.location}
                </span>
              </div>

              <p style={{ fontSize: '15px', color: '#475569', lineHeight: '1.6', marginBottom: '20px' }}>
                {selectedPackage.fullDescription}
              </p>

              {/* Itinerary */}
              <div style={{ marginBottom: '25px' }}>
                <h3 style={{ fontSize: '18px', fontWeight: '700', color: '#0f172a', marginBottom: '15px' }}>
                  Day-by-Day Itinerary
                </h3>
                <div style={{ display: 'grid', gap: '12px' }}>
                  {selectedPackage.itinerary.map((item) => (
                    <div key={item.day} style={{ background: '#f5f7fa', padding: '12px', borderRadius: '4px', borderLeft: '3px solid #003580' }}>
                      <h4 style={{ fontSize: '13px', fontWeight: '700', color: '#003580', marginBottom: '4px' }}>
                        Day {item.day}: {item.title}
                      </h4>
                      <p style={{ fontSize: '12px', color: '#666' }}>{item.desc}</p>
                    </div>
                  ))}
                </div>
              </div>

              {/* Inclusions & Exclusions */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '25px' }}>
                <div>
                  <h4 style={{ fontSize: '14px', fontWeight: '700', color: '#28a745', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <CheckCircle2 size={18} /> Inclusions
                  </h4>
                  <ul style={{ fontSize: '12px', color: '#666', lineHeight: '1.8' }}>
                    {selectedPackage.inclusions.map((inc, i) => (
                      <li key={i} style={{ marginBottom: '6px' }}>✓ {inc}</li>
                    ))}
                  </ul>
                </div>
                <div>
                  <h4 style={{ fontSize: '14px', fontWeight: '700', color: '#ff4757', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <XCircle size={18} /> Exclusions
                  </h4>
                  <ul style={{ fontSize: '12px', color: '#999', lineHeight: '1.8' }}>
                    {selectedPackage.exclusions.map((exc, i) => (
                      <li key={i} style={{ marginBottom: '6px' }}>✗ {exc}</li>
                    ))}
                  </ul>
                </div>
              </div>

              {/* Highlights */}
              <div style={{ marginBottom: '25px', padding: '15px', background: '#f5f7fa', borderRadius: '4px' }}>
                <h4 style={{ fontSize: '13px', fontWeight: '700', color: '#0f172a', marginBottom: '10px' }}>Package Highlights</h4>
                <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                  {selectedPackage.highlights.map((h, i) => (
                    <span key={i} style={{ fontSize: '12px', background: 'white', padding: '6px 12px', borderRadius: '20px', color: '#003580', fontWeight: '600' }}>
                      ⭐ {h}
                    </span>
                  ))}
                </div>
              </div>

              {/* Price & CTA */}
              <div style={{ padding: '20px', background: '#f0f7ff', borderRadius: '4px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <p style={{ fontSize: '11px', color: '#666', marginBottom: '4px' }}>Total Price Per Person</p>
                  <div style={{ display: 'flex', gap: '8px', alignItems: 'baseline' }}>
                    {selectedPackage.originalPrice && (
                      <span style={{ fontSize: '14px', color: '#999', textDecoration: 'line-through' }}>
                        ₹{selectedPackage.originalPrice.toLocaleString()}
                      </span>
                    )}
                    <span style={{ fontSize: '24px', fontWeight: '700', color: '#003580' }}>
                      ₹{selectedPackage.price.toLocaleString()}
                    </span>
                  </div>
                </div>
                <button style={{
                  padding: '12px 30px',
                  background: '#003580',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontWeight: '600',
                  fontSize: '14px',
                  transition: 'all 0.2s'
                }}
                onMouseEnter={(e) => e.target.style.background = '#002a5c'}
                onMouseLeave={(e) => e.target.style.background = '#003580'}>
                  BOOK NOW
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default HolidayPackagesPage;