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

package uk.gov.hmrc.ofstedformsnotifications.client

import uk.gov.service.notify.NotificationClientApi

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.JavaConverters._

class GovNotificationClient(notificationClient: NotificationClientApi)
                           (implicit executionContext: ExecutionContext) extends NotificationFasade {

  override def sendByEmail(template: TemplateId,
                           email: Email,
                           personalization: Map[String, String],
                           reference: Reference): Future[EmailNotification] = {
    Future {
      val response = notificationClient.sendEmail(template.asString, email.value, personalization.asJava, reference.value)
      val responseReference = Option(response.getReference.orElse(null)).map(Reference.apply)
      val fromEmail: Option[String] = Option(response.getFromEmail.orElse(null))
      EmailNotification(
        NotificationId(response.getNotificationId),
        responseReference,
        TemplateId(response.getTemplateId),
        response.getTemplateVersion,
        response.getTemplateUri,
        response.getBody,
        response.getSubject,
        fromEmail
      )
    }
  }
}
