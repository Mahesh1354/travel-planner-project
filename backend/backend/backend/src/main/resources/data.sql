
INSERT INTO users (email, password_hash, full_name, phone) VALUES
('user1@mail.com','h1','User One','9000000001'),
('user2@mail.com','h2','User Two','9000000002'),
('user3@mail.com','h3','User Three','9000000003'),
('user4@mail.com','h4','User Four','9000000004'),
('user5@mail.com','h5','User Five','9000000005'),
('user6@mail.com','h6','User Six','9000000006'),
('user7@mail.com','h7','User Seven','9000000007'),
('user8@mail.com','h8','User Eight','9000000008'),
('user9@mail.com','h9','User Nine','9000000009'),
('user10@mail.com','h10','User Ten','9000000010'),
('user11@mail.com','h11','User Eleven','9000000011'),
('user12@mail.com','h12','User Twelve','9000000012'),
('user13@mail.com','h13','User Thirteen','9000000013'),
('user14@mail.com','h14','User Fourteen','9000000014'),
('user15@mail.com','h15','User Fifteen','9000000015'),
('user16@mail.com','h16','User Sixteen','9000000016'),
('user17@mail.com','h17','User Seventeen','9000000017'),
('user18@mail.com','h18','User Eighteen','9000000018'),
('user19@mail.com','h19','User Nineteen','9000000019'),
('user20@mail.com','h20','User Twenty','9000000020');


INSERT INTO trips (user_id, trip_name, destination_city, destination_country, start_date, end_date, budget_total) VALUES
(1,'Goa Trip','Goa','India','2025-01-10','2025-01-15',40000),
(2,'Manali Trip','Manali','India','2025-02-10','2025-02-15',35000),
(3,'Kerala Tour','Kochi','India','2025-03-01','2025-03-06',50000),
(4,'Dubai Holiday','Dubai','UAE','2025-03-15','2025-03-20',120000),
(5,'Paris Visit','Paris','France','2025-04-05','2025-04-12',150000),
(6,'Jaipur Tour','Jaipur','India','2025-04-20','2025-04-25',30000),
(7,'Singapore Tour','Singapore','Singapore','2025-05-10','2025-05-15',140000),
(8,'Ladakh Adventure','Leh','India','2025-05-20','2025-05-28',70000),
(9,'Bangkok Trip','Bangkok','Thailand','2025-06-01','2025-06-06',90000),
(10,'Ooty Vacation','Ooty','India','2025-06-15','2025-06-20',35000),
(11,'Shimla Tour','Shimla','India','2025-07-01','2025-07-06',30000),
(12,'Maldives Trip','Male','Maldives','2025-07-10','2025-07-15',180000),
(13,'Agra Visit','Agra','India','2025-07-20','2025-07-23',20000),
(14,'Kashmir Trip','Srinagar','India','2025-08-01','2025-08-07',60000),
(15,'Andaman Tour','Port Blair','India','2025-08-10','2025-08-16',75000),
(16,'Rome Trip','Rome','Italy','2025-09-01','2025-09-07',160000),
(17,'London Trip','London','UK','2025-09-15','2025-09-22',170000),
(18,'Bali Trip','Bali','Indonesia','2025-10-01','2025-10-07',110000),
(19,'Nepal Tour','Kathmandu','Nepal','2025-10-15','2025-10-20',45000),
(20,'Darjeeling Trip','Darjeeling','India','2025-11-01','2025-11-06',32000),
(1,'Rishikesh Trip','Rishikesh','India','2025-11-10','2025-11-14',25000),
(2,'Udaipur Trip','Udaipur','India','2025-11-20','2025-11-25',28000),
(3,'Hyderabad Tour','Hyderabad','India','2025-12-01','2025-12-05',26000),
(4,'Pune Visit','Pune','India','2025-12-10','2025-12-13',20000),
(5,'Mumbai Trip','Mumbai','India','2025-12-15','2025-12-20',30000),
(6,'Chennai Trip','Chennai','India','2026-01-05','2026-01-10',27000),
(7,'Kolkata Tour','Kolkata','India','2026-01-15','2026-01-20',26000),
(8,'Mysore Trip','Mysore','India','2026-02-01','2026-02-05',24000),
(9,'Amritsar Trip','Amritsar','India','2026-02-10','2026-02-13',22000),
(10,'Coorg Trip','Coorg','India','2026-02-20','2026-02-25',28000);

INSERT INTO itinerary_items (trip_id,item_date,title,description,category,cost) VALUES
-- Trip 1
(1,'2025-01-10','Flight','Flight Booking','FLIGHT',8000),
(1,'2025-01-10','Hotel','Hotel Check-in','HOTEL',12000),
(1,'2025-01-11','Beach Visit','Beach Activity','ACTIVITY',2000),
(1,'2025-01-12','Local Travel','Taxi Ride','TRANSPORT',1500),
(1,'2025-01-13','Shopping','Market Visit','ACTIVITY',2500),

-- Trip 2
(2,'2025-02-10','Bus Travel','Volvo Bus','TRANSPORT',2500),
(2,'2025-02-10','Hotel','Hotel Stay','HOTEL',11000),
(2,'2025-02-11','Snow Activity','Snow Adventure','ACTIVITY',3000),
(2,'2025-02-12','Sightseeing','Valley Visit','ACTIVITY',2000),
(2,'2025-02-13','Food','Restaurant','FOOD',1500),

-- Trip 3
(3,'2025-03-01','Flight','Flight Booking','FLIGHT',9000),
(3,'2025-03-01','Hotel','Resort Stay','HOTEL',14000),
(3,'2025-03-02','Boat Ride','Houseboat','ACTIVITY',4000),
(3,'2025-03-03','Tea Garden','Munnar Visit','ACTIVITY',2500),
(3,'2025-03-04','Local Travel','Cab Travel','TRANSPORT',1800),


