package br.edu.com.matricula.support;

import org.springframework.test.context.ActiveProfiles;

/**
 * Base para testes de API/integração.
 * <p>
 * Requer PostgreSQL com database {@code matricula_test} (mesmo user/senha do Compose).
 * Ex.: {@code docker compose up -d db} e depois criar o database de teste.
 */
@ActiveProfiles("test")
public abstract class AbstractPostgresIntegrationTest {
}
