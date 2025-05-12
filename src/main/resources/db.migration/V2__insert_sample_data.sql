-- Create extension for UUID generation if not exists
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Insert admin user if it doesn't exist
INSERT INTO users (username, password, email, full_name, enabled, created_at, updated_at)
VALUES
    ('admin',
     '$2a$10$CJgEoobU2gm0euD4ygru4OCrKQ0I.Gd.PpCMw.UKFNHe/zgXqkrby',
     'admin@store.com',
     'Admin User',
     true,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP)
ON CONFLICT (username)
DO NOTHING;

-- Insert admin role
INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ADMIN'
FROM users u
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1
    FROM user_roles ur
    WHERE ur.user_id = u.id
    AND ur.role = 'ADMIN'
  );

-- Insert sample products with better image URLs and proper error handling
INSERT INTO products (
    name,
    description,
    price,
    category,
    image_url,
    stock_quantity,
    active,
    created_at,
    updated_at
)
VALUES
    ('Smartphone X',
     'Latest smartphone with high-end features',
     999.99,
     'Electronics',
     'https://images.pexels.com/photos/1647976/pexels-photo-1647976.jpeg',
     50,
     true,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),
    ('Laptop Pro',
     'Professional laptop for developers',
     1499.99,
     'Electronics',
     'https://images.pexels.com/photos/18105/pexels-photo.jpg',
     30,
     true,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),
    ('Wireless Headphones',
     'Noise-cancelling wireless headphones',
     199.99,
     'Electronics',
     'https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg',
     100,
     true,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),
    ('Smart Watch',
     'Fitness tracking smart watch',
     249.99,
     'Electronics',
     'https://images.pexels.com/photos/437037/pexels-photo-437037.jpeg',
     45,
     true,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),
    ('Cotton T-Shirt',
     'Comfortable cotton t-shirt',
     19.99,
     'Clothing',
     'https://images.pexels.com/photos/428338/pexels-photo-428338.jpeg',
     200,
     true,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),
    ('Jeans',
     'Classic blue jeans',
     49.99,
     'Clothing',
     'https://images.pexels.com/photos/1082529/pexels-photo-1082529.jpeg',
     150,
     true,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),
    ('Coffee Maker',
     'Automatic coffee maker',
     89.99,
     'Home',
     'https://images.pexels.com/photos/1207918/pexels-photo-1207918.jpeg',
     40,
     true,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),
    ('Blender',
     'High-speed blender for smoothies',
     79.99,
     'Home',
     'https://images.pexels.com/photos/3209039/pexels-photo-3209039.jpeg',
     35,
     true,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),
    ('Yoga Mat',
     'Anti-slip yoga mat',
     29.99,
     'Sports',
     'https://images.pexels.com/photos/4056723/pexels-photo-4056723.jpeg',
     80,
     true,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),
    ('Dumbbells Set',
     'Set of adjustable dumbbells',
     149.99,
     'Sports',
     'https://images.pexels.com/photos/949126/pexels-photo-949126.jpeg',
     25,
     true,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

