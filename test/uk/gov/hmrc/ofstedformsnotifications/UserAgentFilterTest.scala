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

import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.stream.ActorMaterializer
import org.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import play.api.http.Status
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.test.FakeRequest
import uk.gov.hmrc.play.bootstrap.filters.FrontendFilters

import scala.concurrent.{Await, Future, duration}
import scala.concurrent.duration.Duration
import play.api.test.Helpers._

class UserAgentFilterTest extends WordSpec with Matchers with MockitoSugar with BeforeAndAfterAll  {

  implicit val system = ActorSystem("user-agent-test")

  val materializer = ActorMaterializer()

  val requestFromExample = FakeRequest("POST", "/").withHeaders("User-Agent" -> "example-service")

  val requestFromOther = FakeRequest("POST", "/").withHeaders("User-Agent" -> "other-service")

  val requestWithoutAgent = FakeRequest("POST", "/")

  "Filter with pattern" should {
    val filter = new UserAgentFilter(materializer, Some(Pattern.compile(".*example.*")))
    "pass matching User-Agents" in {
      status(filter.apply(_ => Future.successful(Results.Ok("PASS")))(requestFromExample)) shouldBe Status.OK
    }
    "denny non matching User-Agents" in {
      status(filter.apply(_ => Future.successful(Results.Ok("PASS")))(requestFromOther)) shouldBe Status.BAD_REQUEST
    }
    "denny without User-Agents" in {
      status(filter.apply(_ => Future.successful(Results.Ok("PASS")))(requestWithoutAgent)) shouldBe Status.BAD_REQUEST
    }
  }

  "Filter without pattern" should {
    val filter = new UserAgentFilter(materializer, None)
    "pass matching User-Agents" in {
      status(filter.apply(_ => Future.successful(Results.Ok("PASS")))(requestFromExample)) shouldBe Status.OK
    }
    "pass non matching User-Agents" in {
      status(filter.apply(_ => Future.successful(Results.Ok("PASS")))(requestFromExample)) shouldBe Status.OK
    }
    "pass without User-Agent" in {
      status(filter.apply(_ => Future.successful(Results.Ok("PASS")))(requestWithoutAgent)) shouldBe Status.OK
    }
  }

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), Duration.apply(30, TimeUnit.SECONDS))
    super.afterAll()
  }
}
