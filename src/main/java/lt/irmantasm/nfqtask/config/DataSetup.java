package lt.irmantasm.nfqtask.config;

import io.r2dbc.spi.ConnectionFactory;

import lt.irmantasm.nfqtask.model.Customer;
import lt.irmantasm.nfqtask.model.Specialist;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class DataSetup {


    String filePath;

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;

    Path path;

    Random random = new Random();

    public DataSetup() {
        this.filePath = "/home/irmantas/Desktop/fake-names.txt";
        this.path = Paths.get(filePath);
    }

    @PostConstruct
    private void populateDb() {
        Mono.fromCallable(() -> Files.readAllLines(path))
                .delaySubscription(Duration.ofSeconds(3))
                .flux()
                .flatMap(Flux::fromIterable)
                .index()
                .map(tuple -> {
                    String[] name = tuple.getT2().split(" ");
                    if (tuple.getT1() < 20L) {
                        customersRepo.save(new Customer(name[2], name[0], name[1]))
                                .subscribe();
                    } else {
                        specialistsRepo.save(new Specialist(name[0], name[1]))
                                .subscribe();
                    }
                    return tuple.getT1();
                })
                .take(40)
                .subscribe();
    }

    @Bean
    @Profile("dev")
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        return initializer;
    }


}
