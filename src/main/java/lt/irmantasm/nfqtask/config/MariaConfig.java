package lt.irmantasm.nfqtask.config;

import io.r2dbc.spi.ConnectionFactory;
import org.mariadb.r2dbc.MariadbConnectionConfiguration;
import org.mariadb.r2dbc.MariadbConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MariaConfig extends AbstractR2dbcConfiguration {

    @Bean
    @Primary
    public MariadbConnectionFactory connectionFactory() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try (InputStream f = loader.getResourceAsStream("db.properties")) {
            props.load(f);

            return new MariadbConnectionFactory(MariadbConnectionConfiguration.builder()
                    .host(props.getProperty("host"))
                    .port(Integer.parseInt(props.getProperty("port")))
                    .username(props.getProperty("username"))
                    .password(props.getProperty("password"))
                    .database(props.getProperty("database"))
                    .build());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
