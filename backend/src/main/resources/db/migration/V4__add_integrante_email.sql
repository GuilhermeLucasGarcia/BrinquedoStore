-- Adiciona email para cadastro e autenticacao de contas.

ALTER TABLE "INTEGRANTE" ADD COLUMN IF NOT EXISTS "EMAIL" VARCHAR(255);

UPDATE "INTEGRANTE"
SET "EMAIL" = COALESCE("EMAIL", LOWER(COALESCE("NOME_USUARIO", REPLACE("NOME", ' ', '.'))) || '@behappy.local');
