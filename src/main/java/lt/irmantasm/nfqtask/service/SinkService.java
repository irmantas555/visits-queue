package lt.irmantasm.nfqtask.service;

import lt.irmantasm.nfqtask.model.MyVisit;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

@Service
public class SinkService {
    public Sinks.Many<MyVisit> replaySink = Sinks.many().replay().all();

    public void  emitMyNext(MyVisit visit){
        replaySink.emitNext(visit, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
