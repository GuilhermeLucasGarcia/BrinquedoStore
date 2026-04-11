# Arquitetura e Estrutura do Projeto: BrinquedoStore

Este documento descreve a organização técnica, padrões arquiteturais e fluxo de dados do projeto **BrinquedoStore**, desenvolvido em Java com Spring Boot.

## Visão Geral da Arquitetura

O projeto adota uma **Arquitetura em Camadas (Layered Architecture)** no padrão monolítico. A renderização das páginas ocorre no lado do servidor (Server-Side Rendering) utilizando a engine de templates **Thymeleaf**.

Essa abordagem garante uma clara separação de preocupações (Separation of Concerns - SoC), facilitando a manutenção, testes e evolução do código.

## Árvore de Diretórios

A estrutura segue as convenções padrão do Maven para projetos Spring Boot:

```text
BrinquedoStore/
└── backend/
    ├── src/
    │   ├── main/
    │   │   ├── java/com/brinquedostore/api/
    │   │   │   ├── config/        # Configurações de inicialização e infraestrutura
    │   │   │   ├── controller/    # Endpoints HTTP (Camada de Apresentação)
    │   │   │   ├── model/         # Entidades JPA (Camada de Domínio)
    │   │   │   ├── repository/    # Interfaces de Banco de Dados (Camada de Persistência)
    │   │   │   └── service/       # Lógica de Negócios (Camada de Serviço)
    │   │   │
    │   │   └── resources/
    │   │       ├── static/        # Arquivos estáticos (CSS, JS, imagens)
    │   │       ├── templates/     # Views HTML processadas pelo Thymeleaf
    │   │       │   ├── admin/     # Telas da área de administração
    │   │       │   └── public/    # Telas públicas (catálogo, detalhes)
    │   │       └── application.properties # Variáveis de ambiente e configuração do Spring
    │   │
    │   └── test/                  # Testes unitários e de integração
    │
    ├── Dockerfile                 # Configuração multi-stage para deploy em contêineres
    └── pom.xml                    # Gerenciador de dependências e build (Maven)
```

## Detalhamento das Camadas

### 1. Model (`/model`)
A camada de Domínio. Contém as classes Java que representam as tabelas do banco de dados (ex: `Brinquedo`, `Categoria`, `Marca`).
*   **Padrões adotados:** Uso intenso de anotações JPA/Hibernate (`@Entity`, `@Table`, relacionamentos como `@ManyToOne`) para mapeamento objeto-relacional (ORM). Uso do Lombok (`@Data`) para redução de código boilerplate (getters/setters).

### 2. Repository (`/repository`)
A camada de Acesso a Dados (Data Access Layer). 
*   **Responsabilidade:** Abstrair todas as operações de banco de dados (CRUD) conectando-se ao PostgreSQL (Supabase).
*   **Padrões adotados:** Utilização de interfaces que estendem `JpaRepository`, aproveitando a criação dinâmica de queries do Spring Data.

### 3. Service (`/service`)
A camada de Negócios.
*   **Responsabilidade:** Orquestrar operações complexas, aplicar regras de negócio e servir como ponte entre os Controllers e os Repositories.
*   **Padrões adotados:** Injeção de dependência via construtor, garantindo que os componentes sejam imutáveis e fáceis de testar (mocking).

### 4. Controller (`/controller`)
A camada de Apresentação/Web.
*   **Responsabilidade:** Interceptar requisições HTTP (`GET`, `POST`), acionar a camada de Service adequada, preparar o modelo de dados (`Model`) e retornar a view correta.
*   **Divisão Lógica:**
    *   `AdminController`: Gerencia rotas de backoffice (criação, edição, listagem de produtos).
    *   `PublicController`: Gerencia rotas acessíveis ao cliente final (vitrine).

### 5. Views (`resources/templates/`)
*   **Responsabilidade:** Interface do Usuário. Como o projeto não possui um front-end desacoplado (como React ou Vue), as telas são arquivos `.html` injetados dinamicamente com dados do servidor através do **Thymeleaf**.

### 6. Config (`/config`)
*   **Responsabilidade:** Classes de bootstrap. Destaca-se o `DataInitializer.java`, responsável por popular o banco com dados de exemplo e realizar ajustes de infraestrutura no banco (como correção de sequências de ID) logo na inicialização da aplicação.

## Fluxo de Dados (Data Flow)

O ciclo de vida de uma requisição típica na aplicação funciona estritamente de forma vertical (Top-Down):

1. **Cliente:** O navegador faz uma requisição HTTP (ex: `/administracao/salvar`).
2. **Controller:** O Spring roteia para o método apropriado. O Controller recebe os dados do formulário (`@ModelAttribute`).
3. **Service:** O Controller chama o método do Service para processar/validar o objeto.
4. **Repository:** O Service aciona o Repository para persistir o dado.
5. **Database:** O Hibernate converte a chamada em SQL e salva no Supabase (PostgreSQL).
6. **Resposta:** O fluxo retorna ao Controller, que decide qual View (HTML) renderizar e enviar de volta ao navegador.

## Boas Práticas Adotadas

*   **Separação de Preocupações (SoC):** Cada camada tem uma responsabilidade única e bem definida.
*   **Injeção de Dependências:** Uso de construtores em vez de `@Autowired` em campos, melhorando a testabilidade e integridade das classes.
*   **Nomenclatura Consistente:** Uso de *PascalCase* para classes e *camelCase* para métodos. Termos de domínio em português para alinhamento com a regra de negócio real.
*   **Cloud-Ready:** Presença de `Dockerfile` otimizado (multi-stage) e configurações flexíveis no `application.properties` para deploy contínuo em plataformas como Render e integração com Supabase Transaction Pooler.
