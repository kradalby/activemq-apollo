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
package org.apache.activemq.apollo.broker.web

import org.apache.activemq.apollo.dto.WebAdminDTO
import org.eclipse.jetty.server.{Connector, Handler, Server}
import org.eclipse.jetty.security._
import org.apache.activemq.apollo.dto.{WebAdminDTO, PrincipalDTO}
import org.apache.activemq.apollo.broker.Broker
import authentication.BasicAuthenticator
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.plus.jaas.JAASLoginService
import org.apache.activemq.apollo.util._
import org.fusesource.hawtdispatch._
import java.io.File
import java.lang.String

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
trait WebServer extends Service

object JettyWebServerFactory extends WebServerFactory.Provider {

  def create(broker:Broker): WebServer = new JettyWebServer(broker)

  def validate(config: WebAdminDTO, reporter: Reporter): ReporterLevel.ReporterLevel = {
    ReporterLevel.INFO
  }
}

object JettyWebServer extends Log

class JettyWebServer(val broker:Broker) extends WebServer with BaseService {
  import JettyWebServer._

  var server:Server = _


  override def toString: String = "jetty webserver"

  protected val dispatch_queue = createQueue()

  protected def _start(on_completed: Runnable) = Broker.BLOCKABLE_THREAD_POOL {
    this.synchronized {
      import OptionSupport._
      import FileSupport._
      import collection.JavaConversions._

      val config = broker.config
      val web_admin = config.web_admin

      val prefix = web_admin.prefix.getOrElse("/")
      val port = web_admin.port.getOrElse(61680)
      val host = web_admin.host.getOrElse("127.0.0.1")

      // Start up the admin interface...
      debug("Starting administration interface");

      if( broker.tmp !=null ) {
        System.setProperty("scalate.workdir", (broker.tmp / "scalate").getCanonicalPath )
      }

      var connector = new SelectChannelConnector
      connector.setHost(host)
      connector.setPort(port)


      val webapp = {
        val x = System.getProperty("apollo.webapp")
        if( x != null ) {
          new File(x)
        } else {
          val home = system_dir("apollo.home")
          val lib = home / "lib"
          lib / lib.list.find( _.matches("""apollo-web-.+-slim.war""")).get
        }
      }

      def admin_app = {
        var app_context = new WebAppContext
        app_context.setContextPath(prefix)
        app_context.setWar(webapp.getCanonicalPath)
        if( broker.tmp !=null ) {
          app_context.setTempDirectory(broker.tmp)
        }
        app_context
      }

      def secured(handler:Handler) = {
        if( config.authentication!=null && config.acl!=null ) {
          val security_handler = new ConstraintSecurityHandler
          val login_service = new JAASLoginService(config.authentication.domain)
          val role_class_names:List[String] = config.authentication.acl_principal_kinds().toList

          login_service.setRoleClassNames(role_class_names.toArray)
          security_handler.setLoginService(login_service)
          security_handler.setIdentityService(new DefaultIdentityService)
          security_handler.setAuthenticator(new BasicAuthenticator)

          val cm = new ConstraintMapping
          val c = new org.eclipse.jetty.http.security.Constraint()
          c.setName("BASIC")
          val admins:Set[PrincipalDTO] = config.acl.admins.toSet
          c.setRoles(admins.map(_.allow).toArray)
          c.setAuthenticate(true)
          cm.setConstraint(c)
          cm.setPathSpec("/*")
          cm.setMethod("GET")
          security_handler.addConstraintMapping(cm)

          security_handler.setHandler(handler)
          security_handler
        } else {
          handler
        }
      }

      server = new Server
      server.setHandler(secured(admin_app))
      server.setConnectors(Array[Connector](connector))
      server.start

      val localPort = connector.getLocalPort
      def url = "http://"+host+":" + localPort + prefix
      info("Administration interface available at: "+url)
      on_completed.run
    }
  }

  protected def _stop(on_completed: Runnable) = Broker.BLOCKABLE_THREAD_POOL {
    this.synchronized {
      server.stop
      server = null
    }
  }

}