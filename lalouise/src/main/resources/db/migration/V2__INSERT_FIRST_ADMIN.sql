-- =============================================================================
-- Admin inicial do sistema. Senha: admin123 (bcrypt)
-- role = 'ADMIN' → Hibernate instancia a classe Admin via discriminador
-- =============================================================================
INSERT INTO credentials (id, role, nickname, email, password, active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'ADMIN',
    'admin',
    'admin@teste.com.br',
    '$2a$12$qx7VCWoLfbT9bBGm0V7WRuYaugXgcqVWhpOuFpthU94c8DDzdE7HK',
    TRUE,
    NOW(),
    NOW()
);

