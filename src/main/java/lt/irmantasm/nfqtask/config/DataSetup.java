package lt.irmantasm.nfqtask.config;

import io.r2dbc.spi.ConnectionFactory;

import lombok.extern.log4j.Log4j2;
import lt.irmantasm.nfqtask.model.*;
import lt.irmantasm.nfqtask.repositories.*;
import lt.irmantasm.nfqtask.service.MySession;
import lt.irmantasm.nfqtask.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Log4j2
@Component
public class DataSetup {
    int customerDbSize = 19;
    int specialistDbSize = 19;

    String filePath;

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;

    @Autowired
    VisitsRepo visitsRepo;

    @Autowired
    CustomRepository customRepository;

    @Autowired
    UtilService utilService;

    @Autowired
    AuthGroupRepo authGroupRepo;

    @Autowired
    MySession mySession;

    Path path;

    Random random = new Random();

    public DataSetup() {
        this.filePath = "/home/irmantas/Desktop/fake-names.txt";
        this.path = Paths.get(filePath);
    }

    @PostConstruct
    private void populateDb() {
        long now = System.currentTimeMillis();
        Random random = new Random();
        //POPULATE CUSTOMERS AND SPECIALISTS
        Mono.fromCallable(() -> Files.readAllLines(path))
                .delaySubscription(Duration.ofSeconds(2))
                .flux()
                .flatMap(Flux::fromIterable)
                .index()
                .map(tuple -> {
                    String[] name = tuple.getT2().split(" ");
                    if (tuple.getT1() <= customerDbSize) {
                        customersRepo.save(new Customer(name[2], name[0], name[1], "customer"))
                                .map(customer -> {
                                    MySession.addCustomer(customer);
                                    return "Ok";
                                })
                                .subscribe();
                    } else {
                        specialistsRepo.save(new Specialist(name[2], name[0], name[1], name[0] + "123"))
                                .map(specialist -> {
                                            MySession.addSpecialist(specialist);
                                            return "Ok";
                                        }
                                )
                                .subscribe();
                        authGroupRepo.save(new AuthGroup(name[2],"SPEC")).subscribe();
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
                        specAppointmentsMap.put(now, tuple.getT1());
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

    @PostConstruct
    private void populateInMemoryVisitsMap() {
        Mono.just("start")
                .delaySubscription(Duration.ofSeconds(3))
                .subscribe();
        customRepository.getVisitsValues();
    }



    @PostConstruct
    private void setSession() {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid);
        String partial = uuid.toString().split("-")[4];
        mySession.setSession(partial);
        final List<Visitor> mmap = new ArrayList<>();
        Flux.interval(Duration.ofSeconds(60))
                .map(v-> {
                    UUID uuid1 = UUID.randomUUID();
                    String partial1 = uuid1.toString().split("-")[4];
                    mySession.setSession(partial1);
                    return "Ok";
                }).subscribe();
    }

    @PostConstruct
    private void visitsCleanupTwo() {
        final List<Visitor> mmap = new ArrayList<>();
        Flux.interval(Duration.ofSeconds(60))
                .doOnNext(i -> System.out.println("Cleanup started"))
                .switchMap((aLong -> Flux.fromIterable(mySession.getVisitMap().entrySet())))
                .map(entry -> {
                    entry.getValue().entrySet().removeIf(visitorEntry -> Instant.ofEpochMilli(visitorEntry.getKey()).plusSeconds(Duration.ofMinutes(15).getSeconds()).toEpochMilli() < System.currentTimeMillis());
                    return "Ok";
                })
                .thenMany(Flux.fromIterable(mySession.getVisitMap().entrySet()))
                .flatMap(entry -> {
                            return Flux.fromIterable(entry.getValue().entrySet())
                                    .filter(lEntry -> {
                                        return Instant.ofEpochMilli(lEntry.getKey()).plusSeconds(Duration.ofMinutes(15).getSeconds()).toEpochMilli() < System.currentTimeMillis();
                                    })
                                    .map(lentry -> lentry.getValue().getVisitId());
                        }
                )
                .doOnNext(id -> System.out.println("Id " + id + "will be deleted"))
                .flatMap(id -> visitsRepo.deleteById(id))
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
