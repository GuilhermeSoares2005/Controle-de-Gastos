package br.com.controledegastos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@Profile("prod")
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "DATABASE_URL")
    public DataSource dataSourceFromUrl() {
        // Se DATABASE_URL estiver presente e no formato postgresql://, converte para JDBC
        if (databaseUrl != null && !databaseUrl.isEmpty() && databaseUrl.startsWith("postgresql://")) {
            try {
                // Extrai componentes da URL postgresql://user:pass@host:port/db
                URI uri = new URI(databaseUrl.replace("postgresql://", "http://"));
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath();
                String dbName = path.startsWith("/") ? path.substring(1) : path;
                
                String userInfo = uri.getUserInfo();
                String username = "";
                String password = "";
                
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    username = parts[0];
                    password = parts.length > 1 ? parts[1] : "";
                }
                
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
                
                return DataSourceBuilder.create()
                        .url(jdbcUrl)
                        .username(username)
                        .password(password)
                        .driverClassName("org.postgresql.Driver")
                        .build();
                        
            } catch (Exception e) {
                throw new RuntimeException("Erro ao converter DATABASE_URL: " + databaseUrl, e);
            }
        }
        
        // Se já estiver no formato JDBC, usa diretamente
        if (databaseUrl != null && databaseUrl.startsWith("jdbc:postgresql://")) {
            return DataSourceBuilder.create()
                    .url(databaseUrl)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        }
        
        throw new IllegalStateException("DATABASE_URL não está no formato esperado: " + databaseUrl);
    }
}

