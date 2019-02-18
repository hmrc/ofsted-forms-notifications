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

import java.util.UUID

import org.scalatest.FlatSpecLike
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.ofstedformsnotifications.client.test.TestNotifications

import scala.concurrent.Await

class TestClientSpec extends FlatSpecLike with ScalaFutures {

  behavior of "Test Client"

  val testNotificationClient = new TestNotifications()

  val template = TemplateId(UUID.randomUUID())



  it should "return successful notification on correct email address" in {
    val correctEmail = Email("ok@example.com")
    testNotificationClient.sendByEmail(template, correctEmail, Map.empty, Reference("correct")).futureValue
  }

  it should "return error notification on error email address" in {
    val errorEmail = Email("error@example.com")
    assertThrows[Exception]{
      Await.result(testNotificationClient.sendByEmail(template, errorEmail, Map.empty, Reference("correct")), patienceConfig.timeout)
    }
  }

  it should "return error notification on other email address" in {
    val otherEmail = Email("example@example.com")
    assertThrows[IllegalStateException] {
      Await.result(testNotificationClient.sendByEmail(template, otherEmail, Map.empty, Reference("correct")), patienceConfig.timeout)
    }
  }

}
