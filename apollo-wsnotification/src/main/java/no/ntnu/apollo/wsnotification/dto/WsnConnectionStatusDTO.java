package no.ntnu.apollo.wsnotification.dto;

import org.apache.activemq.apollo.dto.ConnectionStatusDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Aleksander Skraastad (myth) on 2/24/15.
 * <p/>
 * apollo-project is licenced under the MIT licence.
 */
@XmlRootElement(name="wsn_connection_status")
@XmlAccessorType(XmlAccessType.FIELD)
public class WsnConnectionStatusDTO extends ConnectionStatusDTO {
    // TODO MAEK CLASS
}
