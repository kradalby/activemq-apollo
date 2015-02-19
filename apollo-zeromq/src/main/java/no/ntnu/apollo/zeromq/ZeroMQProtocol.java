package no.ntnu.apollo.zeromq;

import org.apache.activemq.apollo.broker.Connector;
import org.apache.activemq.apollo.broker.DestinationParser;
import org.apache.activemq.apollo.broker.protocol.Protocol;
import org.apache.activemq.apollo.broker.protocol.ProtocolHandler;
import org.fusesource.hawtbuf.AsciiBuffer;

/**
 * Created by kradalby on 19/02/15.
 */
public class ZeroMQProtocol extends ZeroMQProtocolCodecFactory implements Protocol {

    static final DestinationParser destination_parser = new DestinationParser();
    static final AsciiBuffer PROTOCOL_ID = new AsciiBuffer(id);
    static {
        destination_parser.queue_prefix_$eq(null);
        destination_parser.topic_prefix_$eq(null);
        destination_parser.path_separator_$eq("/");
        destination_parser.any_child_wildcard_$eq(null);
        destination_parser.any_descendant_wildcard_$eq(null);
        destination_parser.dsub_prefix_$eq(null);
        destination_parser.temp_queue_prefix_$eq(null);
        destination_parser.temp_topic_prefix_$eq(null);
        destination_parser.destination_separator_$eq(null);
        destination_parser.regex_wildcard_end_$eq(null);
        destination_parser.regex_wildcard_end_$eq(null);
        destination_parser.part_pattern_$eq(null);
    }

    @Override
    public ProtocolHandler createProtocolHandler(Connector connector) {
        return null;
    }
}
