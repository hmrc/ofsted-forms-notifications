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

import uk.gov.service.notify._

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.HashMap


class GovNotificationClient(notificationClient: NotificationClientApi)
                           (implicit executionContext: ExecutionContext) extends NotificationFasade {

  override def sendByEmail(template: TemplateId,
                           email: Email,
                           personalization : HashMap[String, Any],
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

  override def sendBySms(template: TemplateId,
                         phoneNumber: PhoneNumber,
                         personalization: Map[String, String],
                         reference: Reference): Future[SmsNotification] = {
    Future {

      val response = notificationClient.sendSms(template.asString, phoneNumber.value, personalization.asJava, reference.value)

      val responseReference = Option(response.getReference.orElse(null)).map(Reference.apply)
      val fromNumber: Option[String] = Option(response.getFromNumber.orElse(null))

      SmsNotification(
        NotificationId(response.getNotificationId),
        responseReference,
        TemplateId(response.getTemplateId),
        response.getTemplateVersion,
        response.getTemplateUri,
        response.getBody,
        fromNumber
      )
    }
  }

  override def sendDocumentByEmail(template: TemplateId,
                                   email: Email,
                                   personalization: HashMap[String, Any],
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

  override def sendByLetter(template: TemplateId,
                            personalization: HashMap[String, Any],
                            reference: Reference): Future[LetterNotification] = {
    Future {
      val response = notificationClient.sendLetter(template.asString, personalization.asJava, reference.value)
      val responseReference = Option(response.getReference.orElse(null)).map(Reference.apply)
      LetterNotification(
        NotificationId(response.getNotificationId),
        responseReference,
        TemplateId(response.getTemplateId),
        response.getTemplateVersion,
        response.getTemplateUri,
        response.getBody,
        response.getSubject
      )
    }
  }

  override def sendByPrecompiledLetter(reference: Reference, file : File) : Future[LetterUploadNotification] = {
    Future {
      val response = notificationClient.sendPrecompiledLetter(reference.value, file)
      val responseReference = Option(response.getReference.orElse(null)).map(Reference.apply)
      LetterUploadNotification(NotificationId(response.getNotificationId), responseReference)
    }
  }

  override def sendByPrecompiledLetterWithInputStream(reference: Reference, fileIOStream : FileInputStream) : Future[LetterUploadNotification] = {
    Future {
      val response = notificationClient.sendPrecompiledLetterWithInputStream(reference.value, fileIOStream)
      val responseReference = Option(response.getReference.orElse(null)).map(Reference.apply)
      LetterUploadNotification(NotificationId(response.getNotificationId), responseReference)
    }
  }

  override def getNotificationById(notifcationId : NotificationId) : Future[NotificationResponse] = {
    Future {
      val response = notificationClient.getNotificationById(notifcationId.value.toString)
      NotificationResponse(response.getId,
        Option(response.getReference.orElse(null)).map(Reference.apply),
        Option(response.getEmailAddress.orElse(null)).map(Email.apply),
        Option(response.getPhoneNumber.orElse(null)),
        Option(response.getLine1.orElse(null)),
        Option(response.getLine2.orElse(null)),
        Option(response.getLine3.orElse(null)),
        Option(response.getLine4.orElse(null)),
        Option(response.getLine5.orElse(null)),
        Option(response.getLine6.orElse(null)),
        Option(response.getPostcode.orElse(null)),
        Option(response.getNotificationType),
        response.getStatus,
        response.getTemplateId,
        response.getTemplateVersion,
        response.getTemplateUri,
        response.getBody,
        Option(response.getSubject.orElse(null)),
        Option(response.getCreatedAt),
        Option(response.getSentAt.orElse(null)),
        Option(response.getCompletedAt.orElse(null)),
        Option(response.getEstimatedDelivery.orElse(null)),
        Option(response.getCreatedByName.orElse(null))
      )
    }
  }

  override def getNotifications(status : String, notificationType : String, reference: Reference, olderThanId : String) : Future[NotificationList] = {
    Future {
      val response = notificationClient.getNotifications(status, notificationType, reference.value, olderThanId)
      NotificationList(
        response.getNotifications.asScala.toList,
        response.getCurrentPageLink,
        Option(response.getNextPageLink.orElse(null))
      )
    }
  }

  override def getTemplateById(templateId : TemplateId) : Future[TemplateResponse] = {
    Future {
      val response = notificationClient.getTemplateById(templateId.asString)
      TemplateResponse(
        response.getId,
        response.getName,
        response.getTemplateType,
        response.getCreatedAt,
        Option(response.getUpdatedAt.orElse(null)),
        response.getCreatedBy,
        response.getVersion,
        response.getBody(),
        Option(response.getSubject.orElse(null)),
        Option((response.getPersonalisation.orElse(null)).asScala.toMap)
      )
    }

  }

  override def getTemplateByIdAndVersion(templateId : TemplateId, version : Int) : Future[TemplateResponse] = {
    Future {
      val response = notificationClient.getTemplateVersion(templateId.asString, version)
      TemplateResponse(
        response.getId,
        response.getName,
        response.getTemplateType,
        response.getCreatedAt,
        Option(response.getUpdatedAt.orElse(null)),
        response.getCreatedBy,
        response.getVersion,
        response.getBody(),
        Option(response.getSubject.orElse(null)),
        Option((response.getPersonalisation.orElse(null)).asScala.toMap)
      )
    }
  }

  override def getAllTemplates(templateType : String) : Future[List[TemplateResponse]] = {
    Future {
      val response = notificationClient.getAllTemplates(templateType)
      response.getTemplates.asScala.foldLeft(List[TemplateResponse]()) {
        (carryOver, iterElem) => {
          carryOver ++ List(TemplateResponse(
            iterElem.getId,
            iterElem.getName,
            iterElem.getTemplateType,
            iterElem.getCreatedAt,
            Option(iterElem.getUpdatedAt.orElse(null)),
            iterElem.getCreatedBy,
            iterElem.getVersion,
            iterElem.getBody(),
            Option(iterElem.getSubject.orElse(null)),
            Option((iterElem.getPersonalisation.orElse(null)).asScala.toMap)
          ))
        }
      }
    }
  }

  override def getTemplatePreview(template: TemplateId, personalization: HashMap[String, Object]): Future[TemplatePreviewResponse] = {
    Future{
      val response = notificationClient.generateTemplatePreview(template.asString, personalization.asJava)
      TemplatePreviewResponse(
        response.getId,
        response.getTemplateType,
        response.getVersion,
        response.getBody,
        Option(response.getSubject.orElse(null))
      )
    }
  }

  override def getReceivedTextMessages(template: TemplateId): Future[ReceivedTextMessageResponse] = {
    Future{
      val response = notificationClient.getReceivedTextMessages(template.asString)
      val receivedTextMessagesList = response.getReceivedTextMessages.asScala.foldLeft(List[ReceivedTextMessageItem]()) {
        (carryOver, iterElem) => {
          carryOver ++ List(ReceivedTextMessageItem(
            iterElem.getId,
            iterElem.getNotifyNumber,
            iterElem.getUserNumber,
            iterElem.getServiceId,
            iterElem.getContent,
            iterElem.getCreatedAt
          ))
        }
      }

      ReceivedTextMessageResponse(receivedTextMessagesList,
        response.getCurrentPageLink,
        Option(response.getNextPageLink.orElse(null))
      )
    }
  }
}
