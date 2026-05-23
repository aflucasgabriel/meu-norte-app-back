-- ─────────────────────────────────────────────────────────────────────────────
-- Seed de categorias (PostgreSQL)
--
-- Para adicionar uma nova categoria basta inserir aqui (ou direto no banco):
--   INSERT INTO categorias (nome) VALUES ('investment') ON CONFLICT DO NOTHING;
--
-- O ON CONFLICT DO NOTHING garante idempotência: reexecutar não duplica dados.
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO categorias (id, nome) VALUES (1, 'transport') ON CONFLICT (id) DO NOTHING;
INSERT INTO categorias (id, nome) VALUES (2, 'entertainment') ON CONFLICT (id) DO NOTHING;
INSERT INTO categorias (id, nome) VALUES (3, 'utilities') ON CONFLICT (id) DO NOTHING;
INSERT INTO categorias (id, nome) VALUES (4, 'healthcare') ON CONFLICT (id) DO NOTHING;
INSERT INTO categorias (id, nome) VALUES (5, 'shopping') ON CONFLICT (id) DO NOTHING;
INSERT INTO categorias (id, nome) VALUES (6, 'salary') ON CONFLICT (id) DO NOTHING;
INSERT INTO categorias (id, nome) VALUES (7, 'freelance') ON CONFLICT (id) DO NOTHING;