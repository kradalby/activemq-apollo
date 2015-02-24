package no.ntnu.apollo.wsnotification;

import org.apache.activemq.apollo.broker.*;
import org.apache.activemq.apollo.broker.protocol.AbstractProtocolHandler;
import org.apache.activemq.apollo.broker.security.SecurityContext;
import org.apache.activemq.apollo.util.Log$;
import org.apache.activemq.apollo.util.Scala2Java;

/**
 * Created by Aleksander Skraastad (myth) on 2/24/15.
 * <p/>
 * apollo-project is licenced under the MIT licence.
 */
public class WsnProtocolHandler extends AbstractProtocolHandler {

    final SecurityContext security_context = new SecurityContext();
    public static final Scala2Java.Logger log = new Scala2Java.Logger(Log$.MODULE$.apply(WsnProtocolHandler.class));

    public static <T> T received(T value) {
        log.trace("received: %s", value);
        return value;
    }

    @Override
    public String protocol() {
        return "wsn";
    }

    @Override
    public void async_die(String client_message) {

    }

    @Override
    public String session_id() {
        return security_context.session_id();
    }
}
