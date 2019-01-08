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

import com.typesafe.config.ConfigFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import uk.gov.service.notify.NotificationClient
import java.nio.file.{Files, Paths}

import scala.collection.mutable
import scala.concurrent._

class GovNotificationClientSpec extends AsyncFlatSpec with Matchers {

  behavior of "Gov Notification Client"

  val config = ConfigFactory.load()

  val govNotification = new NotificationClient(config.getString("notifications.gov.api-key"))

  val client = new GovNotificationClient(govNotification)(ExecutionContext.global)

  val template = TemplateId("f0b80f67-9782-4178-89ff-52e4ac8de447")

  val smsTemplate = TemplateId("565c4748-25a2-4adf-be86-1edcabf02c87")

  val sendDocumentByEMailTemplate = TemplateId("f0b80f67-9782-4178-89ff-52e4ac8de447")

  val sendLetterTemplate = TemplateId("a5317714-3dee-4bc1-ac4d-963e0d4049c1")

  val email = Email("mohan.rao.dolla@digital.hmrc.gov.uk")

  val phoneNumber = PhoneNumber("+447849105908")

  val smsNotificationId = NotificationId("a63c07dc-1394-4f79-a0f5-867b48d21d91")

  it should "send email notification" in {

    val personalisation = new mutable.HashMap[String, Any]
    personalisation.put("link_to_document", "SomeString from Mohan")

    client.sendByEmail(template, email, personalisation, Reference("ala-ma-kota")).map { result =>
      result.notificationId shouldNot equal (null)
      result.reference shouldNot be (empty)
    }
  }

  it should "send sms notification" in {
    client.sendBySms(smsTemplate, phoneNumber, Map.empty, Reference("ala-ma-kota-sms")).map { result =>
      println("Mohan details = " + result.toString)
      result.notificationId shouldNot equal (null)
      result.reference shouldNot be (empty)
    }
  }

  it should "send document by email notification" in {

    val fileContents = Files.readAllBytes(Paths.get("./Mohan-Nov,2018-Invoice.pdf"))
    val personalisation = new mutable.HashMap[String, Any]
    personalisation.put("link_to_document", fileContents)

    client.sendDocumentByEmail(sendDocumentByEMailTemplate, email, personalisation, Reference("ala-ma-kota-docuiment-by-email")).map { result =>
      result.notificationId shouldNot equal (null)
      result.reference shouldNot be (empty)
    }
  }

  it should "send letter notification" in {

    val personalisation = new mutable.HashMap[String, Any]
    personalisation.put("address_line_1", "33"); // mandatory address field
    personalisation.put("address_line_2", "HopeGreen"); // mandatory address field
    personalisation.put("postcode", "WD25 7HQ "); // mandatory address field
    personalisation.put("first_name", "Mohan"); // field from template
    personalisation.put("application_date", "2019-01-07"); // field from template

    client.sendByLetter(sendLetterTemplate,  personalisation, Reference("ala-ma-kota-letter")).map { result =>
      result.notificationId shouldNot equal (null)
      result.reference shouldNot be (empty)
    }
  }

  it should "send pre-compiled letter notification" in {

    val precompiledPDF = new File("./Mohan-Nov,2018-Invoice.pdf")
    client.sendByPrecompiledLetter(Reference("ala-ma-kota-precompiled-letter"), precompiledPDF).map { result =>
      result.notificationId shouldNot equal (null)
      result.reference shouldNot be (empty)
    }
  }

  it should "send pre-compiled letter notification with InputStream" in {

    val precompiledPDFWithInputStream = new FileInputStream("./Mohan-Nov,2018-Invoice.pdf")
    client.sendByPrecompiledLetterWithInputStream(Reference("ala-ma-kota-precompiled-letter"), precompiledPDFWithInputStream).map { result =>
      result.notificationId shouldNot equal (null)
      result.reference shouldNot be (empty)
    }
  }

  it should "get notification by notificationId" in {
    client.getNotificationById(smsNotificationId).map { result =>
      result.templateId shouldNot equal (null)
      result.reference shouldNot be (empty)
    }
  }

  it should "get all notifications olderthanId" in {
    client.getNotifications("delivered", null, Reference("get-All-notifications-sms"), null).map { result =>
      result.notifications shouldNot be (empty)
    }
  }

  it should "get template by templateId" in {
    client.getTemplateById(sendDocumentByEMailTemplate).map { result =>
      result.body shouldNot be (empty)
    }
  }

  it should "get template by templateId and version" in {
    client.getTemplateByIdAndVersion(sendDocumentByEMailTemplate, 5).map { result =>
      result.body shouldNot be (empty)
    }
  }

  it should "get all templates by templateType" in {
    client.getAllTemplates(null).map { result =>
      result.size should be > 0
    }
  }

  it should "get templatePreview by templateId and personalisation" in {
    val personalisation = new mutable.HashMap[String, Object]
    personalisation.put("link_to_document", "This is personalisation text")
    client.getTemplatePreview(sendDocumentByEMailTemplate, personalisation).map { result =>
      result.body shouldNot be (empty)
    }
  }

  it should "get all messages older than a given templateId" in {
    client.getReceivedTextMessages(TemplateId("d179a38a-9e54-423d-8c2e-673d7eae5f76")).map { result =>
      result.receivedTextMessagesList shouldNot be (empty)
    }
  }

}
