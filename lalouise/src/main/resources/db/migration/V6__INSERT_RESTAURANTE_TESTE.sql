INSERT INTO restaurants (id, name, cnpj, active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'RestauranteTeste',
    '11222333000181',
    TRUE,
    NOW(),
    NOW()
);

