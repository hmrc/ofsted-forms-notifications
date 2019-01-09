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

import javax.inject.Inject
import uk.gov.service.notify._

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}


class GovNotificationClient @Inject()(notificationClient: NotificationClientApi)
                                     (implicit executionContext: ExecutionContext) extends NotificationFacade {

  override def sendByEmail(template: TemplateId,
                           email: Email,
                           personalization: Map[String, Any],
                           reference: Reference): Future[EmailNotification] = {
    Future {
      val response = notificationClient.sendEmail(template.asString, email.value, personalization.asJava, reference.value)
      val responseReference = toOption(response.getReference).map(Reference.apply)
      val fromEmail: Option[String] = toOption(response.getFromEmail)
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

      val responseReference = toOption(response.getReference).map(Reference.apply)
      val fromNumber: Option[String] = toOption(response.getFromNumber)

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
                                   personalization: Map[String, Any],
                                   reference: Reference): Future[EmailNotification] = {
    Future {
      val response = notificationClient.sendEmail(template.asString, email.value, personalization.asJava, reference.value)
      val responseReference = toOption(response.getReference).map(Reference.apply)
      val fromEmail: Option[String] = toOption(response.getFromEmail)
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
                            address: Address,
                            personalization: Map[String, Any],
                            reference: Reference): Future[LetterNotification] = {
    Future {
      val addressParameters = Seq[Option[(String, Any)]](
        Some("address_line_1" -> address.line1),
        Some("address_line_2" -> address.line2),
        Some("postcode" -> address.postcode),
        address.line3.map(line => "address_line_3" -> line),
        address.line4.map(line => "address_line_4" -> line),
        address.line5.map(line => "address_line_5" -> line),
        address.line6.map(line => "address_line_6" -> line)
      ).flatten
      val allPersonalization = personalization ++ addressParameters
      val response = notificationClient.sendLetter(template.asString, allPersonalization.asJava, reference.value)
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

  override def sendByPrecompiledLetter(reference: Reference, file: File): Future[LetterUploadNotification] = {
    Future {
      val response = notificationClient.sendPrecompiledLetter(reference.value, file)
      val responseReference = Option(response.getReference.orElse(null)).map(Reference.apply)
      LetterUploadNotification(NotificationId(response.getNotificationId), responseReference)
    }
  }

  override def sendByPrecompiledLetter(reference: Reference, fileIOStream: FileInputStream): Future[LetterUploadNotification] = {
    Future {
      val response = notificationClient.sendPrecompiledLetterWithInputStream(reference.value, fileIOStream)
      val responseReference = Option(response.getReference.orElse(null)).map(Reference.apply)
      LetterUploadNotification(NotificationId(response.getNotificationId), responseReference)
    }
  }

  override def getNotificationById(notificationId: NotificationId): Future[NotificationResponse] = {
    Future {
      val response = notificationClient.getNotificationById(notificationId.asString)
      val line3 = toOption(response.getLine3)
      val line4 = toOption(response.getLine4)
      val line5 = toOption(response.getLine5)
      val line6 = toOption(response.getLine6)

      val address: Option[Address] = for {
        line1 <- toOption(response.getLine1)
        line2 <- toOption(response.getLine2)
        postCode <- toOption(response.getPostcode)
      } yield Address(line1, line2, line3, line4, line5, line6, postCode)

      NotificationResponse(
        NotificationId(response.getId),
        toOption(response.getReference).map(Reference.apply),
        toOption(response.getEmailAddress).map(Email.apply),
        toOption(response.getPhoneNumber).map(PhoneNumber.apply),
        address,
        Option(response.getNotificationType),
        response.getStatus,
        TemplateId(response.getTemplateId),
        response.getTemplateVersion,
        response.getTemplateUri,
        response.getBody,
        toOption(response.getSubject),
        Option(response.getCreatedAt),
        toOption(response.getSentAt),
        toOption(response.getCompletedAt),
        toOption(response.getEstimatedDelivery),
        toOption(response.getCreatedByName)
      )
    }
  }


  override def getNotifications(status: String, notificationType: String, reference: Reference, olderThanId: String): Future[NotificationList] = {
    Future {
      val response = notificationClient.getNotifications(status, notificationType, reference.value, olderThanId)
      NotificationList(
        response.getNotifications.asScala.toList,
        response.getCurrentPageLink,
        toOption(response.getNextPageLink)
      )
    }
  }

  override def getTemplateById(templateId: TemplateId): Future[TemplateResponse] = {
    Future {
      val response = notificationClient.getTemplateById(templateId.asString)
      TemplateResponse(
        response.getId,
        response.getName,
        response.getTemplateType,
        response.getCreatedAt,
        toOption(response.getUpdatedAt),
        response.getCreatedBy,
        response.getVersion,
        response.getBody(),
        toOption(response.getSubject),
        toOption(response.getPersonalisation).map(_.asScala.toMap).getOrElse(Map.empty)
      )
    }
  }

  override def getTemplateByIdAndVersion(templateId: TemplateId, version: Int): Future[TemplateResponse] = {
    Future {
      val response = notificationClient.getTemplateVersion(templateId.asString, version)
      TemplateResponse(
        response.getId,
        response.getName,
        response.getTemplateType,
        response.getCreatedAt,
        toOption(response.getUpdatedAt),
        response.getCreatedBy,
        response.getVersion,
        response.getBody(),
        toOption(response.getSubject),
        toOption(response.getPersonalisation).map(_.asScala.toMap).getOrElse(Map.empty)
      )
    }
  }

  override def getAllTemplates(templateType: String): Future[List[TemplateResponse]] = {
    Future {
      val response = notificationClient.getAllTemplates(templateType)
      response.getTemplates.asScala.foldLeft(List[TemplateResponse]()) {
        (carryOver, iterElem) => {
          carryOver ++ List(TemplateResponse(
            iterElem.getId,
            iterElem.getName,
            iterElem.getTemplateType,
            iterElem.getCreatedAt,
            toOption(iterElem.getUpdatedAt),
            iterElem.getCreatedBy,
            iterElem.getVersion,
            iterElem.getBody,
            toOption(iterElem.getSubject),
            toOption(iterElem.getPersonalisation).map(_.asScala.toMap).getOrElse(Map.empty)
          ))
        }
      }
    }
  }

  override def getTemplatePreview(template: TemplateId, personalization: Map[String, Object]): Future[TemplatePreviewResponse] = {
    Future {
      val response = notificationClient.generateTemplatePreview(template.asString, personalization.asJava)
      TemplatePreviewResponse(
        response.getId,
        response.getTemplateType,
        response.getVersion,
        response.getBody,
        toOption(response.getSubject)
      )
    }
  }

  override def getReceivedTextMessages(template: TemplateId): Future[ReceivedTextMessageResponse] = {
    Future {
      val response = notificationClient.getReceivedTextMessages(template.asString)
      val receivedTextMessages: List[ReceivedTextMessageItem] = response.getReceivedTextMessages.asScala.map {
        iterElem =>
          ReceivedTextMessageItem(
            iterElem.getId,
            iterElem.getNotifyNumber,
            iterElem.getUserNumber,
            iterElem.getServiceId,
            iterElem.getContent,
            iterElem.getCreatedAt
          )
      }(collection.breakOut)

      ReceivedTextMessageResponse(
        receivedTextMessages,
        response.getCurrentPageLink,
        toOption(response.getNextPageLink)
      )
    }
  }

  private def toOption[T <: AnyRef](option: java.util.Optional[T]): Option[T] = {
    Option(option.orElse(null.asInstanceOf[T]))
  }
}
