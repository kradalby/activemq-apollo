package no.ntnu.apollo.zeromq;

import org.apache.activemq.apollo.broker.Connector;
import org.apache.activemq.apollo.broker.protocol.ProtocolCodecFactory;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtdispatch.transport.ProtocolCodec;

public class ZeroMQProtocolCodecFactory implements ProtocolCodecFactory.Provider {

    @Override
    public String id() {
        return null;
    }

    @Override
    public ProtocolCodec createProtocolCodec(Connector connector) {
        return null;
    }

    @Override
    public boolean isIdentifiable() {
        return false;
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
