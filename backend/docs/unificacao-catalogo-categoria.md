# Unificação de `Catalogo` e `Categoria`

## Contexto

O sistema passou a manter duas estruturas para o mesmo conceito de organização do catálogo:

- `Categoria`: entidade já usada por `Brinquedo`, catálogo público e filtros por categoria.
- `Catalogo`: entidade administrativa criada posteriormente, com atributos de gestão como descrição, status e data de criação.

Essa duplicidade gerava:

- sobreposição funcional entre cadastro de catálogo e cadastro de categoria;
- inconsistência conceitual entre área pública e administrativa;
- aumento de custo de manutenção;
- risco de divergência de dados.

## Decisão de modelagem

A estrutura canônica passa a ser **`Categoria`**.

Motivos:

- já é a entidade referenciada por `Brinquedo`;
- já sustenta a navegação pública em `/catalogo`;
- evita migração de chave estrangeira em `BRINQUEDO`;
- reduz impacto funcional e técnico.

## Mudanças realizadas

### 1. Expansão da entidade `Categoria`

`Categoria` passou a incorporar os campos administrativos antes presentes em `Catalogo`:

- `descricao`
- `ativo`
- `dtCriacao`
- `dtAlteracao`
- `imgUrl`

### 2. Remoção da camada redundante

Foram removidos os artefatos específicos da entidade `Catalogo`:

- model
- repository
- service
- controller
- initializer de schema dedicado

### 3. CRUD administrativo preservado

A funcionalidade administrativa continua acessível em:

- `/administracao/catalogos`

Porém, agora ela opera sobre `Categoria`, que se tornou a única fonte de verdade.

### 4. Migração de dados

Foi criado o script:

- `src/main/resources/db/migration/V1__unify_catalogo_categoria.sql`

O script:

- adiciona em `CATEGORIA` os campos ausentes;
- normaliza dados legados;
- cria backup em `CATALOGO_BACKUP`;
- copia registros de `CATALOGO` para `CATEGORIA` sem duplicar nomes;
- remove a tabela `CATALOGO` ao final.

### 5. Execução automática da migração

O componente:

- `CatalogoCategoriaMigrationInitializer`

executa o script SQL automaticamente no startup da aplicação.

## Impacto da refatoração

### Impacto positivo

- elimina duplicidade de entidades;
- melhora coesão do domínio;
- reduz acoplamento desnecessário;
- simplifica manutenção do catálogo público e administrativo;
- preserva compatibilidade com a estrutura já usada por `Brinquedo`.

### Impacto controlado

- o módulo admin passa a editar `Categoria`;
- o campo `imgUrl` também passa a ser gerenciado na tela administrativa de catálogos;
- exclusão de categoria agora bloqueia registros ligados a produtos.

## Estratégia de integridade

- backup dos dados antigos em `CATALOGO_BACKUP`;
- migração idempotente baseada em nome;
- proteção contra exclusão de categoria vinculada a produtos;
- testes automatizados para controller e repository.

## Testes atualizados

Foram criados/ajustados testes para validar:

- segurança de acesso ao admin;
- CRUD administrativo do catálogo unificado;
- filtros e paginação;
- integridade de exclusão;
- consultas de categorias ativas.

## Observações

- o nome funcional da rota `/administracao/catalogos` foi mantido para evitar quebra de navegação e de UX;
- a entidade persistida deixou de ser `Catalogo` e passou a ser exclusivamente `Categoria`.
