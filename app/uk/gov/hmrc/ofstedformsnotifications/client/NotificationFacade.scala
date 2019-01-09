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

import java.io.{File, FileInputStream}
import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json.Reads
import uk.gov.service.notify.Notification

import scala.concurrent.Future

final case class TemplateId(value: UUID) {
  def asString = value.toString
}

object TemplateId {
  def apply(value: String): TemplateId = new TemplateId(UUID.fromString(value))

  implicit val reads: Reads[TemplateId] = Reads.StringReads.map(apply)
}

final case class Email(value: String)

object Email {
  // FIXME thing about this should be implicit...
  implicit val reads: Reads[Email] = Reads.StringReads.map(apply)
}

final case class PhoneNumber(value: String)

final case class Reference(value: String)

object Reference {
  implicit val reads: Reads[Reference] = Reads.StringReads.map(apply)
}

final case class NotificationId(value: UUID) {
  def asString = value.toString
}

object NotificationId {
  def apply(value: String): NotificationId = new NotificationId(UUID.fromString(value))

  implicit val reads: Reads[NotificationId] = Reads.StringReads.map(apply)
}

final case class EmailNotification(notificationId: NotificationId,
                                   reference: Option[Reference],
                                   templateId: TemplateId,
                                   templateVersion: Int,
                                   templateUri: String,
                                   body: String,
                                   subject: String,
                                   fromEmail: Option[String])

final case class SmsNotification(notificationId: NotificationId,
                                 reference: Option[Reference],
                                 templateId: TemplateId,
                                 templateVersion: Int,
                                 templateUri: String,
                                 body: String,
                                 fromNumber: Option[String])

final case class LetterNotification(notificationId: NotificationId,
                                    reference: Option[Reference],
                                    templateId: TemplateId,
                                    templateVersion: Int,
                                    templateUri: String,
                                    body: String,
                                    subject: String)

final case class LetterUploadNotification(notificationId: NotificationId,
                                          reference: Option[Reference])

final case class Address(line1: String,
                         line2: String,
                         line3: Option[String],
                         line4: Option[String],
                         line5: Option[String],
                         line6: Option[String],
                         postcode: String)

final case class NotificationResponse(id: NotificationId,
                                      reference: Option[Reference],
                                      emailAddress: Option[Email],
                                      phoneNumber: Option[PhoneNumber],
                                      address: Option[Address],
                                      notificationType: Option[String],
                                      status: String,
                                      templateId: TemplateId,
                                      templateVersion: Int,
                                      templateUri: String,
                                      body: String,
                                      subject: Option[String],
                                      createdAt: Option[DateTime],
                                      sentAt: Option[DateTime],
                                      completedAt: Option[DateTime],
                                      estimatedDelivery: Option[DateTime],
                                      createdByName: Option[String])

//FIXME abstraction leak - class from implemenattion in external API
final case class NotificationList(notifications: List[Notification], currentPageLink: String, nextPageLink: Option[String])

final case class TemplateResponse(id: UUID,
                                  name: String,
                                  templateType: String,
                                  createdAt: DateTime,
                                  updatedAt: Option[DateTime],
                                  createdBy: String,
                                  version: Int,
                                  body: String,
                                  subject: Option[String],
                                  personalisation: Map[String, Any])

final case class TemplatePreviewResponse(id: UUID,
                                         templateType: String,
                                         version: Int,
                                         body: String,
                                         subject: Option[String])

final case class ReceivedTextMessageResponse(receivedTextMessagesList: List[ReceivedTextMessageItem],
                                             currentPageLink: String,
                                             nextPageLink: Option[String])

final case class ReceivedTextMessageItem(id: UUID,
                                         notifyNumber: String,
                                         userNumber: String,
                                         serviceId: UUID,
                                         content: String,
                                         createdAt: DateTime)


trait NotificationFacade {

  def sendByEmail(template: TemplateId,
                  email: Email,
                  personalization: Map[String, Any],
                  reference: Reference): Future[EmailNotification]

  def sendBySms(template: TemplateId,
                phoneNumber: PhoneNumber,
                personalization: Map[String, String],
                reference: Reference): Future[SmsNotification]

  def sendDocumentByEmail(template: TemplateId,
                          email: Email,
                          personalization: Map[String, Any],
                          reference: Reference): Future[EmailNotification]

  def sendByLetter(template: TemplateId,
                   address: Address,
                   personalization: Map[String, Any],
                   reference: Reference): Future[LetterNotification]

  def sendByPrecompiledLetter(reference: Reference, file: File): Future[LetterUploadNotification]

  def sendByPrecompiledLetter(reference: Reference, fileIOStream: FileInputStream): Future[LetterUploadNotification]

  def getNotificationById(notificationId: NotificationId): Future[NotificationResponse]

  def getNotifications(status: String,
                       notificationType: String,
                       reference: Reference,
                       olderThanId: String): Future[NotificationList]

  def getTemplateById(template: TemplateId): Future[TemplateResponse]

  def getTemplateByIdAndVersion(template: TemplateId, version: Int): Future[TemplateResponse]

  def getAllTemplates(templateType: String): Future[List[TemplateResponse]]

  def getTemplatePreview(template: TemplateId, personalization: Map[String, Object]): Future[TemplatePreviewResponse]

  def getReceivedTextMessages(template: TemplateId): Future[ReceivedTextMessageResponse]

}
