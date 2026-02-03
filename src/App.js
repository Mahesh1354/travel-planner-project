import React, { useState } from 'react';
import './App.css';

function App() {
  // State for FR3 (Itinerary Management) and FR10 (Budget Tracking)
  const [trips, setTrips] = useState([]);
  const [destination, setDestination] = useState('');
  const [budget, setBudget] = useState('');

  // Function to add a trip (FR3)
  const addTrip = (e) => {
    e.preventDefault();
    const newTrip = {
      id: Date.now(),
      destination: destination,
      budget: parseFloat(budget) || 0
    };
    setTrips([...trips, newTrip]);
    
    // FR4: Reset form fields
    setDestination('');
    setBudget('');
  };

  // Logic for FR10: Automatic Budget Calculation
  const totalBudget = trips.reduce((sum, trip) => sum + trip.budget, 0);

  return (
    <div className="App">
      <nav>
        <h1>🌍 Global Nomad</h1>
      </nav>

      <main>
        {/* Input Form Section */}
        <section className="form-container">
          <form onSubmit={addTrip}>
            <input 
              type="text" 
              placeholder="Destination (e.g. Tokyo)" 
              value={destination}
              onChange={(e) => setDestination(e.target.value)}
              required 
            />
            <input 
              type="number" 
              placeholder="Budget ($)" 
              value={budget}
              onChange={(e) => setBudget(e.target.value)}
              required 
            />
            <button type="submit">Add to Itinerary</button>
          </form>
        </section>

        {/* Stats Display - FR10 */}
        <section className="summary-stats">
          <p>Total Trips: {trips.length}</p>
          <p>Total Budget: ${totalBudget.toLocaleString()}</p>
        </section>

        {/* Trip List - FR3 */}
        <div id="tripsContainer">
          {trips.map(trip => (
            <div key={trip.id} className="trip-card">
              <div>
                <h3>{trip.destination}</h3>
                <p>Budget: ${trip.budget.toLocaleString()}</p>
              </div>
              <button 
                className="delete-btn" 
                onClick={() => setTrips(trips.filter(t => t.id !== trip.id))}
              >
                Remove
              </button>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}

// CRITICAL: This line fixes the "export default not found" error
export default App;