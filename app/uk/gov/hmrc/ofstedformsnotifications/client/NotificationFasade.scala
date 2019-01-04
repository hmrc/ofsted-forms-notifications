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

import play.api.libs.json.Reads
import scala.concurrent.Future
import java.util.UUID

final case class TemplateId(value: UUID) {
  def asString = value.toString
}

object TemplateId {
  def apply(value: String): TemplateId = new TemplateId(UUID.fromString(value))
  implicit val reads: Reads[TemplateId] = Reads.StringReads.map(apply(_))
}

final case class Email(value: String)

final case class Reference(value: String)

object Reference {
  implicit val reads: Reads[Reference] = Reads.StringReads.map(apply(_))
}

final case class NotificationId(value: UUID)

object NotificationId {
  def apply(value: String): NotificationId = new NotificationId(UUID.fromString(value))
  implicit val reads: Reads[NotificationId] = Reads.StringReads.map(apply(_))
}

final case class EmailNotification(notificationId: NotificationId,
                                   reference: Option[Reference],
                                   templateId: TemplateId,
                                   templateVersion: Int,
                                   templateUri: String,
                                   body: String,
                                   subject: String,
                                   fromEmail: Option[String])

trait NotificationFasade {
  def sendByEmail(template: TemplateId,
                  email: Email,
                  personalization: Map[String, String],
                  reference: Reference): Future[EmailNotification]
}
