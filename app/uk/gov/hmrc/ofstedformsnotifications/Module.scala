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

import com.google.inject.{AbstractModule, Provides}
import javax.inject.{Named, Singleton}
import play.api.Configuration
import uk.gov.hmrc.ofstedformsnotifications.client.{GovNotificationClient, NotificationFacade, TemplateId}
import uk.gov.service.notify.{NotificationClient, NotificationClientApi}

class Module extends AbstractModule {

  @Singleton
  @Provides
  def templateConfiguration(configuration: Configuration): TemplateConfiguration = {
    TemplateConfiguration(
      submission = TemplateId(configuration.get[String]("notifications.templates.submission")),
      acceptance = TemplateId(configuration.get[String]("notifications.templates.acceptance")),
      rejection = TemplateId(configuration.get[String]("notifications.templates.rejection"))
    )
  }

  @Singleton
  @Provides
  def govNotification(configuration: Configuration): NotificationClientApi = {
    new NotificationClient(configuration.get[String]("notifications.gov.api-key"))
  }

  @Provides
  @Singleton
  @Named("rejection-url")
  def rejectionUrl(configuration: Configuration): String = {
    configuration.get[String]("notifications.rejection.url")
  }

  override def configure(): Unit = {
    bind(classOf[NotificationFacade]).to(classOf[GovNotificationClient])
  }
}
