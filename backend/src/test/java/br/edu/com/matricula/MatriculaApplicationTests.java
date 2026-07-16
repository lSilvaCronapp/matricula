package br.edu.com.matricula;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Placeholder do esqueleto (Fase 0).
 * Testes de domínio ficam no backlog; este teste exige banco disponível
 * e será habilitado/ajustado nas fases seguintes.
 */
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class MatriculaApplicationTests {

    @Test
    void contextLoads() {
    }
}
