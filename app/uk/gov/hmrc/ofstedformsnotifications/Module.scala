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

import com.google.inject.AbstractModule
import javax.inject.{Named, Singleton}
import play.api.Configuration
import uk.gov.hmrc.ofstedformsnotifications.config.{AnalyticsDetails, ReportProblemLinkDetails}

class Module extends AbstractModule {

  @Singleton
  def analyticsProvider(configuration: Configuration): AnalyticsDetails =  {
    AnalyticsDetails(
      token = configuration.get[String]("google-analytics.token"),
      host = configuration.get[String]("google-analytics.host")
    )
  }

  @Singleton
  def reportProblemLinks(configuration: Configuration): ReportProblemLinkDetails = {
    val host = configuration.get[String]("contact-frontend.host")
    val identifier = configuration.get[String]("contact-frontend.service-identifier")
    ReportProblemLinkDetails(
      ajax = s"$host/contact/problem_reports_ajax?service=$identifier",
      noJs = s"$host/contact/problem_reports_nonjs?service=$identifier"
    )
  }

  override def configure(): Unit = {

  }
}
