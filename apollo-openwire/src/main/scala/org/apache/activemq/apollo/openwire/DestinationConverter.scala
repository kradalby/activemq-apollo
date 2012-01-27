/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.apollo.openwire

import org.apache.activemq.apollo.dto.{TopicDestinationDTO, QueueDestinationDTO, DestinationDTO}
import org.apache.activemq.apollo.broker.DestinationParser
import org.apache.activemq.apollo.openwire.command._

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
object DestinationConverter {

  val OPENWIRE_PARSER = new DestinationParser();
  OPENWIRE_PARSER.path_separator = "."
  OPENWIRE_PARSER.any_child_wildcard = "*"
  OPENWIRE_PARSER.any_descendant_wildcard = ">"

  //  = Pattern.compile("[ a-zA-Z0-9\\_\\-\\%\\~]")
  
  def to_destination_dto(dest: ActiveMQDestination, handler:OpenwireProtocolHandler): Array[DestinationDTO] = {

    if( !dest.isComposite ) {
      import ActiveMQDestination._
      var name = dest.getPhysicalName
      Array(dest.getDestinationType match {
        case QUEUE_TYPE =>
          var path_parts = OPENWIRE_PARSER.parts(name).map(OPENWIRE_PARSER.sanitize_destination_part(_, true))
          new QueueDestinationDTO(path_parts)
        case TOPIC_TYPE =>
          var path_parts = OPENWIRE_PARSER.parts(name).map(OPENWIRE_PARSER.sanitize_destination_part(_, true))
          new TopicDestinationDTO(path_parts)
        case TEMP_QUEUE_TYPE =>
          val (connectionid, rest)= name.splitAt(name.lastIndexOf(':'))
          val real_path = ("temp" :: handler.broker.id :: OPENWIRE_PARSER.sanitize_destination_part(connectionid) :: OPENWIRE_PARSER.sanitize_destination_part(rest.substring(1)) :: Nil).toArray
          new QueueDestinationDTO( real_path ).temp(true)
        case TEMP_TOPIC_TYPE =>
          val (connectionid, rest)= name.splitAt(name.lastIndexOf(':'))
          val real_path = ("temp" :: handler.broker.id :: OPENWIRE_PARSER.sanitize_destination_part(connectionid) :: OPENWIRE_PARSER.sanitize_destination_part(rest.substring(1)) :: Nil).toArray
          new TopicDestinationDTO( real_path ).temp(true)
      })
    } else {
      dest.getCompositeDestinations.map { c =>
        to_destination_dto(c, handler)(0)
      }
    }
  }

  def to_activemq_destination(dest:Array[DestinationDTO]):ActiveMQDestination = {
    import collection.JavaConversions._

    val rc = dest.flatMap { dest =>

      val temp = dest.path.headOption == Some("temp")
      dest match {
        case dest:QueueDestinationDTO =>
          if( temp ) {
            Some(new ActiveMQTempQueue(dest.path.toList.drop(2).map(OPENWIRE_PARSER.unsanitize_destination_part(_)).mkString(":")))
          } else {
            val name = OPENWIRE_PARSER.encode_path(asScalaBuffer(dest.path).toList.map(OPENWIRE_PARSER.unsanitize_destination_part(_)))
            Some(new ActiveMQQueue(name))
          }
        case dest:TopicDestinationDTO =>
          if( temp ) {
            Some(new ActiveMQTempTopic(dest.path.toList.drop(2).map(OPENWIRE_PARSER.unsanitize_destination_part(_)).mkString(":")))
          } else {
            val name = OPENWIRE_PARSER.encode_path(asScalaBuffer(dest.path).toList.map(OPENWIRE_PARSER.unsanitize_destination_part(_)))
            Some(new ActiveMQTopic(name))
          }
        case _ => None 
      }
    }

    if( rc.length == 0) {
      null
    } else if( rc.length == 1) {
      rc(0)
    } else {
      val c = new ActiveMQQueue()
      c.setCompositeDestinations(rc)
      c
    }

  }
}