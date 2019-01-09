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

import com.typesafe.config.ConfigFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import uk.gov.service.notify.NotificationClient

import scala.concurrent._

class GovNotificationClientSpec extends AsyncFlatSpec with Matchers {

  behavior of "Gov Notification Client"

  val config = ConfigFactory.load()

  val govNotification = new NotificationClient(config.getString("notifications.gov.api-key"))

  val client = new GovNotificationClient(govNotification)(ExecutionContext.global)

  val template = TemplateId("f0b80f67-9782-4178-89ff-52e4ac8de447")

  val email = Email("mohan.rao.dolla@digital.hmrc.gov.uk")
  
  it should "send email notification" in {

    val personalisation = Map[String, Any]("link_to_document" -> "SomeString from Mohan")

    client.sendByEmail(template, email, personalisation, Reference("ala-ma-kota")).map { result =>
      result.notificationId shouldNot equal(null)
      result.reference shouldNot be(empty)
    }
  }
}
