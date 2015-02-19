package no.ntnu.apollo.zeromq;

import org.apache.activemq.apollo.broker.Broker;
import org.apache.activemq.apollo.broker.protocol.AbstractProtocolHandler;
import org.apache.activemq.apollo.util.Fn0;
import org.apache.activemq.apollo.util.Log$;
import org.apache.activemq.apollo.util.Scala2Java;
import org.fusesource.hawtdispatch.DispatchQueue;

/**
 * Created by kradalby on 19/02/15.
 */
public class ZeroMQProtocolHandler extends AbstractProtocolHandler {

    public static final Scala2Java.Logger log = new Scala2Java.Logger(Log$.MODULE$.apply(ZeroMQProtocolHandler.class));

    public static <T> T received(T value) {
        log.trace("received: %s", value);
        return value;
    }

    static Fn0<String> WAITING_ON_CLIENT_REQUEST = new Fn0<String>() {
        @Override
        public String apply() {
            return "client request";
        }
    };

    @Override
    public String protocol() { return "zeromq"; }

    public Broker broker() { return connection().connector().broker(); }

    public DispatchQueue queue() {
        return connection().dispatch_queue();
    }

    @Override
    public void async_die(String client_message) {

    }

    @Override
    public String session_id() {
        return null;
    }
}
