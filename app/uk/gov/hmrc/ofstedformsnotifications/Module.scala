/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ofstedformsnotifications

import java.net.{Authenticator, InetSocketAddress, Proxy}
import java.util.regex.Pattern

import com.google.inject.{AbstractModule, Provides}
import javax.inject.{Named, Provider, Singleton}
import play.api.{ConfigLoader, Configuration}
import uk.gov.hmrc.ofstedformsnotifications.client.gov.{GovNotificationClient, ProxyAuthenticator}
import uk.gov.hmrc.ofstedformsnotifications.client.test.TestNotifications
import uk.gov.hmrc.ofstedformsnotifications.client.{NotificationFacade, TemplateId}
import uk.gov.service.notify.{NotificationClient, NotificationClientApi}

class Module extends AbstractModule {

  @Singleton
  @Provides
  def templateConfiguration(configuration: Configuration): TemplateConfiguration = {
    def parseTemplateId(config: Map[String, String]): Map[String, TemplateId] = config.map {
      case (key, value) => (key, TemplateId(value))
    }
    val loadConfig = (configuration.get[Map[String, String]] _).andThen(parseTemplateId)

    TemplateConfiguration(
      submission = loadConfig("notifications.templates.submission"),
      acceptance = loadConfig("notifications.templates.acceptance"),
      rejection = loadConfig("notifications.templates.rejection")
    )
  }

  /**
    * Authenticator related code comes from section - "How to use the Authenticator class"
    * [1] https://docs.oracle.com/javase/8/docs/technotes/guides/net/http-auth.html
    */
  @Provides
  @Singleton
  def proxyConfiguration(configuration: Configuration): Option[Proxy] = {
    if(configuration.get[Boolean]("proxy.proxyRequiredForThisEnvironment")){
      val host = configuration.get[String]("proxy.host")
      val port = configuration.get[Int]("proxy.port")(ConfigLoader.stringLoader.map(_.toInt))
      val username = configuration.get[String]("proxy.username")
      val password = configuration.get[String]("proxy.password")
      Authenticator.setDefault(new ProxyAuthenticator(username, password.toCharArray)) // [1] look on javadoc
      val address = new InetSocketAddress(host, port)
      Some(new Proxy(Proxy.Type.HTTP, address))
    } else {
      None
    }
  }

  @Singleton
  @Provides
  def govNotification(configuration: Configuration, proxy: Option[Proxy]): NotificationClientApi = {
    val apiKey = configuration.get[String]("notifications.gov.api-key")
    proxy.map { proxy =>
      new NotificationClient(apiKey, proxy)
    }.getOrElse(new NotificationClient(apiKey))
  }

  @Provides
  @Singleton
  @Named("rejection-url")
  def rejectionUrl(configuration: Configuration): String = {
    configuration.get[String]("notifications.rejection.url")
  }

  @Provides
  @Singleton
  def userAgentPattern(configuration: Configuration): Option[Pattern] = {
    configuration.get[Option[String]]("authorization.user-agent").map(Pattern.compile)
  }

  @Provides
  @Singleton
  def notificationFacadeProvider(configuration: Configuration,
                                 govProvider: Provider[GovNotificationClient],
                                 testProvider: Provider[TestNotifications]): NotificationFacade = {
    configuration.get[String]("notifications.provider") match {
      case "gov.uk" => govProvider.get()
      case "test" => testProvider.get()
    }
  }

  override def configure(): Unit = {

  }
}
