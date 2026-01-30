Database Design – Travel Planner Application

1. users
- id
- name
- email
- password
- role
- created_at

2. trips
- id
- user_id
- trip_name
- start_date
- end_date
- destination
- created_at

3. trip_collaborators
- id
- trip_id
- user_id

4. itineraries
- id
- trip_id
- date
- notes

5. activities
- id
- itinerary_id
- activity_name
- location
- time
- cost

6. bookings
- id
- trip_id
- booking_type (flight/hotel/activity)
- provider
- booking_reference
- booking_date
- cost

7. budgets
- id
- trip_id
- estimated_budget
- actual_spent

8. notifications
- id
- user_id
- message
- type
- created_at
- is_read
