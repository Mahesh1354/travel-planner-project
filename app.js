document.addEventListener('DOMContentLoaded', () => {
    // Select HTML elements based on the IDs in your index.html
    const tripForm = document.getElementById('tripForm');
    const tripsContainer = document.getElementById('tripsContainer');
    const tripCountEl = document.getElementById('trip-count');
    const totalBudgetEl = document.getElementById('total-budget-sum');

    // Initialize tracking variables for FR10
    let tripCount = 0;
    let totalBudget = 0;

    // Listen for the "submit" event on the form
    tripForm.addEventListener('submit', (e) => {
        // IMPORTANT: Prevent the browser from refreshing the page
        e.preventDefault(); 

        // Get values from the input fields
        const dest = document.getElementById('dest-input').value;
        const date = document.getElementById('date-input').value;
        const budgetVal = parseFloat(document.getElementById('budget-input').value) || 0;

        // FR3: Create a new Trip Card element
        const card = document.createElement('div');
        card.className = 'trip-card'; // This uses your defined CSS class
        card.innerHTML = `
            <h3>🌍 ${dest}</h3>
            <p><strong>Departure:</strong> ${date}</p>
            <p><strong>Budget:</strong> $${budgetVal.toLocaleString()}</p>
            <button class="delete-btn" style="background:#e74c3c; color:white; border:none; padding:8px; border-radius:4px; cursor:pointer; margin-top:10px;">Remove Trip</button>
        `;

        // Add Delete Functionality (CRUD: Delete)
        card.querySelector('.delete-btn').addEventListener('click', () => {
            card.remove();
            tripCount--;
            totalBudget -= budgetVal;
            updateStats();
        });

        // Add the new card to the list
        tripsContainer.appendChild(card);
        
        // Update stats for Budget Management (FR10)
        tripCount++;
        totalBudget += budgetVal;
        updateStats();

        // FR4: Reset the form for the next entry
        tripForm.reset();
    });

    // Helper function to update the summary numbers on the screen
    function updateStats() {
        tripCountEl.innerText = tripCount;
        totalBudgetEl.innerText = totalBudget.toLocaleString();
    }
});