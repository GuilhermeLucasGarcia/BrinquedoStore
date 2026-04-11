-- Unifica a entidade administrativa Catalogo na entidade Categoria.
-- A migracao preserva os dados existentes por meio de backup e copia idempotente.

ALTER TABLE "CATEGORIA" ADD COLUMN IF NOT EXISTS "DESCRICAO" VARCHAR(500);
ALTER TABLE "CATEGORIA" ADD COLUMN IF NOT EXISTS "ATIVO" BOOLEAN;
ALTER TABLE "CATEGORIA" ADD COLUMN IF NOT EXISTS "DT_CRIACAO" TIMESTAMP;

UPDATE "CATEGORIA"
SET "DESCRICAO" = COALESCE("DESCRICAO", CONCAT('Catalogo ', "NOME")),
    "ATIVO" = COALESCE("ATIVO", TRUE),
    "DT_CRIACAO" = COALESCE("DT_CRIACAO", "DT_ALTERACAO", CURRENT_TIMESTAMP);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'CATALOGO'
    ) THEN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.tables
            WHERE table_name = 'CATALOGO_BACKUP'
        ) THEN
            EXECUTE $sql$CREATE TABLE "CATALOGO_BACKUP" (LIKE "CATALOGO" INCLUDING ALL)$sql$;
        END IF;

        EXECUTE $sql$
            INSERT INTO "CATALOGO_BACKUP"
            SELECT c.*
            FROM "CATALOGO" c
            WHERE NOT EXISTS (
                SELECT 1
                FROM "CATALOGO_BACKUP" backup
                WHERE backup.id = c.id
            )
        $sql$;

        EXECUTE $sql$
            INSERT INTO "CATEGORIA" ("NOME", "IMG_URL", "DT_ALTERACAO", "DESCRICAO", "ATIVO", "DT_CRIACAO")
            SELECT c."NOME",
                   NULL,
                   COALESCE(c."DT_ALTERACAO", CURRENT_TIMESTAMP),
                   COALESCE(c."DESCRICAO", CONCAT('Catalogo ', c."NOME")),
                   COALESCE(c."ATIVO", TRUE),
                   COALESCE(c."DT_CRIACAO", c."DT_ALTERACAO", CURRENT_TIMESTAMP)
            FROM "CATALOGO" c
            WHERE NOT EXISTS (
                SELECT 1
                FROM "CATEGORIA" categoria
                WHERE LOWER(categoria."NOME") = LOWER(c."NOME")
            )
        $sql$;

        EXECUTE $sql$DROP TABLE IF EXISTS "CATALOGO" CASCADE$sql$;
    END IF;
END $$;
