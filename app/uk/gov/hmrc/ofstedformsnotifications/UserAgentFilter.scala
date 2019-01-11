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

import java.util.regex.Pattern

import akka.stream.Materializer
import javax.inject.Inject
import play.api.http.HeaderNames
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.filters.FrontendFilters

import scala.concurrent.Future

class UserAgentFilter @Inject()(val mat: Materializer,
                                optionalPattern: Option[Pattern]) extends Filter {

  private val notMatchResult = Future.successful(Results.BadRequest("User-Agent does not match requirements"))

  override def
  apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    val userAgent = rh.headers.get(HeaderNames.USER_AGENT).getOrElse("")
    optionalPattern.fold(f(rh)) { pattern =>
      if (pattern.matcher(userAgent).matches()) {
        f(rh)
      } else {
        notMatchResult
      }
    }
  }
}
