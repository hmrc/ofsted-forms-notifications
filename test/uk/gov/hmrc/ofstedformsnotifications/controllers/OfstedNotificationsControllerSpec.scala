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

package uk.gov.hmrc.ofstedformsnotifications.controllers

import java.time.{Instant, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util.UUID

import akka.stream.Materializer
import org.mockito
import org.mockito.ArgumentMatchersSugar
import org.mockito.integrations.scalatest.ResetMocksAfterEachTest
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Request}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.retrieve.EmptyRetrieval
import uk.gov.hmrc.auth.core.{AuthConnector, Enrolment}
import uk.gov.hmrc.ofstedformsnotifications.{FormNotification, TemplateConfiguration}
import uk.gov.hmrc.ofstedformsnotifications.client._

import scala.concurrent.{ExecutionContext, Future}


class OfstedNotificationsControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite
  with mockito.MockitoSugar with ArgumentMatchersSugar with ResetMocksAfterEachTest with BeforeAndAfterEach {

  implicit val materializer: Materializer = app.injector.instanceOf[Materializer]

  val exampleNotification = FormNotification(
    id = UUID.randomUUID().toString,
    email = Email("lukasz.dubiel@digital.hmrc.gov.uk"),
    time = ZonedDateTime.now()
  )

  val fakeRequest: Request[FormNotification] = FakeRequest("POST", "/")
    .withBody(exampleNotification)
    .withHeaders(CONTENT_TYPE -> "application/json")

  val notificationMock: NotificationFacade = mock[NotificationFacade]

  val submissionTemplate = TemplateId(UUID.randomUUID())

  val acceptanceTemplate = TemplateId(UUID.randomUUID())

  val rejectionTemplate = TemplateId(UUID.randomUUID())

  val authMock: AuthConnector = mock[AuthConnector]

  def setupAuth(id: Option[String] = Some("id"),
                email: Option[String] = Some("test@example.com"),
                enrolments: Set[Enrolment] = Set.empty) = {
    val authResult = Future.successful(())
    when(authMock.authorise(any, eqTo(EmptyRetrieval))(any, any)) thenReturn authResult
  }

  def setupNotification(template: TemplateId): Unit = {
    when(notificationMock.sendByEmail(eqTo(template), any, any, any)) thenReturn Future.successful(
      EmailNotification(
        NotificationId(UUID.randomUUID()),
        None,
        template,
        0,
        "",
        "",
        "",
        None
      )
    )
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    setupAuth()
  }

  val controller = new OfstedNotifications(
    cc = stubControllerComponents(),
    notifications = notificationMock,
    templates = TemplateConfiguration(submissionTemplate, acceptanceTemplate, rejectionTemplate),
    rejectionUrl = "http://www.gov.uk/",
    authConnector = authMock
  )(ExecutionContext.global)

  "POST /submisssion" should {
    "return 200" in {
      setupNotification(submissionTemplate)
      val result = controller.submission.apply(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return UUID" in {
      setupNotification(submissionTemplate)
      val result = controller.submission(fakeRequest)
      contentType(result) shouldBe Some("text/plain")
      charset(result) shouldBe Some("utf-8")
      UUID.fromString(contentAsString(result))
    }
  }

  "POST /acceptance" should {
    "return 200" in {
      setupNotification(acceptanceTemplate)
      val result = controller.acceptance(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return UUID" in {
      setupNotification(acceptanceTemplate)
      val result = controller.acceptance(fakeRequest)
      contentType(result) shouldBe Some("text/plain")
      charset(result) shouldBe Some("utf-8")
      UUID.fromString(contentAsString(result))
    }
  }

  "POST /rejection" should {
    "return 200" in {
      setupNotification(rejectionTemplate)
      val result = controller.rejection(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return UUID" in {
      setupNotification(rejectionTemplate)
      val result = controller.rejection(fakeRequest)
      contentType(result) shouldBe Some("text/plain")
      charset(result) shouldBe Some("utf-8")
      UUID.fromString(contentAsString(result))
    }
  }
}