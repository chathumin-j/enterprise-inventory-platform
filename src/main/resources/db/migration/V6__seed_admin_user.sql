-- default administrator account pass "Admin@12345"
INSERT INTO users (username, email, password, role, enabled)
VALUES (
    'admin',
    'admin@inventory.local',
    '$2b$10$l/q8F1qZAXz2C4DaJZm0DuUBBaK96QMhJpsy8JXhLo9Yepa6ibAXG',
    'ADMIN',
    TRUE
);
