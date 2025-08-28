-- Insert Restaurants
INSERT INTO restaurant (id, name, address, is_open) VALUES
(default, 'Pasta Palace', '123 Main St', TRUE),
(default, 'Sushi Central', '456 Ocean Ave', TRUE),
(default, 'Burger Barn', '789 Elm St', FALSE),
(default, 'Taco Town', '321 Maple Rd', TRUE),
(default, 'Pizza Plaza', '654 Oak St', TRUE);


-- Uncomment the following line if you want to reset the ID sequence for menu_item
--  ALTER TABLE restaurant ALTER COLUMN id RESTART WITH 6;

-- Insert Dishes
INSERT INTO menu_item (id, name, price, restaurant_id) VALUES
(default, 'Spaghetti Carbonara', 12.99, 1),
(default, 'Fettuccine Alfredo', 11.99, 1),
(default, 'Lasagna', 13.99, 1),
(default, 'Bruschetta', 7.99, 1),

(default, 'California Roll', 8.99, 2),
(default, 'Salmon Nigiri', 10.99, 2),
(default, 'Tempura Shrimp', 9.99, 2),
(default, 'Miso Soup', 4.99, 2),

(default, 'Classic Burger', 10.49, 3),
(default, 'Cheeseburger', 11.49, 3),
(default, 'Veggie Burger', 9.99, 3),
(default, 'Fries', 3.99, 3),

(default, 'Chicken Taco', 3.49, 4),
(default, 'Beef Taco', 3.99, 4),
(default, 'Fish Taco', 4.49, 4),
(default, 'Guacamole', 5.99, 4),

(default, 'Margherita Pizza', 10.99, 5),
(default, 'Pepperoni Pizza', 12.49, 5),
(default, 'BBQ Chicken Pizza', 13.49, 5),
(default, 'Garlic Bread', 6.99, 5);

-- Uncomment the following line if you want to reset the ID sequence for menu_item
--  ALTER TABLE menu_item ALTER COLUMN id RESTART WITH 21;