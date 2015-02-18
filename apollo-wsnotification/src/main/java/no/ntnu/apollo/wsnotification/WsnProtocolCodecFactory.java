package no.ntnu.apollo.wsnotification;

import org.apache.activemq.apollo.broker.Broker$;
import org.apache.activemq.apollo.broker.Connector;
import org.apache.activemq.apollo.broker.protocol.ProtocolCodecFactory;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtdispatch.transport.ProtocolCodec;

/**
 * Created by Aleksander Skraastad (myth) on 2/18/15.
 * <p/>
 * apollo-project is licenced under the MIT licence.
 */
public class WsnProtocolCodecFactory implements ProtocolCodecFactory.Provider {

    static final String id = "wsnotification";

    @Override
    public String id() {
        return id;
    }

    @Override
    public ProtocolCodec createProtocolCodec(Connector connector) {
        return null;
    }

    @Override
    public boolean isIdentifiable() {
        return true;
    }

    @Override
    public int maxIdentificaionLength() {
        return 0;
    }

    @Override
    public boolean matchesIdentification(Buffer buffer) {
        return false;
    }
}
