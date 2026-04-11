package com.brinquedostore.api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class CatalogoCategoriaMigrationInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public CatalogoCategoriaMigrationInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        String[] migrations = {
                "db/migration/V1__unify_catalogo_categoria.sql",
                "db/migration/V2__expand_integrante_usuarios.sql"
        };

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            for (String migration : migrations) {
                ClassPathResource resource = new ClassPathResource(migration);
                String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                statement.execute(sql);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao executar as migrações de banco de dados da aplicação.", ex);
        }
    }
}
