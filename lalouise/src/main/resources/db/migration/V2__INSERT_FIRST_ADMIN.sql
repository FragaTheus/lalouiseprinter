INSERT INTO admins (id, nickname, email, password, role, active, created_at, updated_at)
VALUES (
           gen_random_uuid(),
           'admin',
           'admin@teste.com.br',
           '$2a$12$qx7VCWoLfbT9bBGm0V7WRuYaugXgcqVWhpOuFpthU94c8DDzdE7HK',
           'ADMIN',
           TRUE,
           NOW(),
           NOW()
       );