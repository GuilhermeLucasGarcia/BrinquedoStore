-- Adiciona perfil de acesso para distinguir clientes de funcionarios.

ALTER TABLE "INTEGRANTE" ADD COLUMN IF NOT EXISTS "PERFIL" VARCHAR(30);

UPDATE "INTEGRANTE"
SET "PERFIL" = COALESCE("PERFIL", 'FUNCIONARIO');
