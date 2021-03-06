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

import java.time.ZonedDateTime

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ofstedformsnotifications.client.Email

class FormNotificationReadsTest extends FlatSpec with Matchers {

  val exampleJson: JsValue = Json.parse(
    """{
      |  "time" : "2004-02-12T15:19:21+00:00",
      |  "email" : "jan.kowalski@example.com",
      |  "id" : "da7740d5-6026-4cdd-bbc1-10cb077cc47b",
      |  "kind" : "SC1"
      |}
    """.stripMargin
  )

  "FormNotification reads" should "produce correct form Notification" in {
    val notification = FormNotification.reads.reads(exampleJson).fold(
      err => fail(s"Error in reads ${err.toString}"),
      notification => {
        notification.time shouldEqual ZonedDateTime.parse("2004-02-12T15:19:21+00:00")
        notification.email shouldEqual Email("jan.kowalski@example.com")
        notification.id shouldEqual "da7740d5-6026-4cdd-bbc1-10cb077cc47b"
      }
    )

  }
}
