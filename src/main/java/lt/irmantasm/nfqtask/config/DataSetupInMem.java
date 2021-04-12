package lt.irmantasm.nfqtask.config;

import io.r2dbc.spi.ConnectionFactory;

import lombok.extern.log4j.Log4j2;
import lt.irmantasm.nfqtask.model.*;
import lt.irmantasm.nfqtask.repositories.*;
import lt.irmantasm.nfqtask.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;

@Log4j2
@Component
@Profile("dev")
public class DataSetupInMem {
    int customerDbSize = 19;
    int specialistDbSize = 19;
        Resource resfile = new ClassPathResource("fake-names.txt");

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;

    @Autowired
    VisitsRepo visitsRepo;

    @Autowired
    AuthGroupRepo authGroupRepo;

    @Autowired
    UtilService utilService;

    @PostConstruct
    private void populateDb() {
        long now = System.currentTimeMillis();
        Random random = new Random();
        //POPULATE CUSTOMERS AND SPECIALISTS
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(5);
        String customPwd = encoder.encode("Cust" + 123);
        Mono.fromCallable(() -> Files.readAllLines(resfile.getFile().toPath()))
                .delaySubscription(Duration.ofSeconds(2))
                .flux()
                .flatMap(Flux::fromIterable)
                .index()
                .map(tuple -> {
                    String[] name = tuple.getT2().split(" ");
                    if (tuple.getT1() <= customerDbSize) {
                        customersRepo.save(new Customer(name[2], name[0], name[1], customPwd))
                                .map(customer -> {
                                    return customer;
                                })
                                .flatMap(customer -> authGroupRepo.save(new AuthGroup(customer.getEmail(), "USER")))
                                .subscribe();
                    } else {
                        String pwd = encoder.encode(name[0] + 123);
                        specialistsRepo.save(new Specialist(name[2], name[0], name[1], pwd))
                                .map(specialist -> {
                                            return specialist;
                                        }
                                )
                                .flatMap(specialist -> authGroupRepo.save(new AuthGroup(specialist.getEmail(), "USER")))
                                .subscribe();
                    }
                    return tuple.getT1();
                })
                .take(customerDbSize + specialistDbSize + 2)
                .subscribe();
        //POPULATE VISITS
        Tuple2<Integer, Integer> of = Tuples.of(random.nextInt(customerDbSize) + 1, random.nextInt(specialistDbSize) + 1);
        Flux.generate(() -> of, (state, sink) -> {
            sink.next(state);
            return Tuples.of(random.nextInt(customerDbSize) + 1, random.nextInt(specialistDbSize) + 1);
        })
                .map(v -> (Tuple2<Integer, Integer>) v)
                .delaySubscription(Duration.ofSeconds(3))
                .take(20)
//                .doOnNext(tuple2 -> log.info("CustommerId: {}, SpecialistIf {}", tuple2.getT1(), tuple2.getT2()))
                .reduce(new HashMap<Integer, TreeMap<Long, Integer>>(), (hashmap, tuple) -> {
                    if (null == hashmap || null == hashmap.get(tuple.getT2())) {
                        TreeMap specAppointmentsMap = new TreeMap();
                        specAppointmentsMap.put((now + (random.nextInt(60) + 15) * 60 * 1000), tuple.getT1());
                        hashmap.put(tuple.getT2(), specAppointmentsMap);
                    } else {
                        TreeMap specAppointmentsMap = hashmap.get(tuple.getT2());
                        Map.Entry<Long, Integer> lastEntry = specAppointmentsMap.lastEntry();
                        long lastTime = lastEntry.getKey();
                        long nextTime = utilService.nextTime(lastTime);
                        specAppointmentsMap.put(nextTime, tuple.getT1());
                    }
                    return hashmap;
                })
                .flux()
                .flatMap(hashMap -> Flux.fromIterable(hashMap.entrySet()))
                .flatMap(integerTreeMapEntry -> Flux.fromIterable(integerTreeMapEntry.getValue().entrySet())
                        .map(longIntegerEntry -> {
                            String sserial = utilService.getSerial(longIntegerEntry.getKey());
                            return new Visit(integerTreeMapEntry.getKey().longValue(), longIntegerEntry.getValue().longValue(), longIntegerEntry.getKey(), 15, sserial);
                        })
                )
                .flatMap(visit -> visitsRepo.save(visit))
                .subscribe();
    }

    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        return initializer;
    }


}
