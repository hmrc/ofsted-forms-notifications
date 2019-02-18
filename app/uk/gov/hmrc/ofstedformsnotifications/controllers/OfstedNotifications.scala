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

import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util

import javax.inject.{Inject, Named, Singleton}
import play.api.mvc._
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.ofstedformsnotifications.client.{NotificationFacade, Reference}
import uk.gov.hmrc.ofstedformsnotifications.{FormNotification, TemplateConfiguration}
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class OfstedNotifications @Inject()(cc: ControllerComponents,
                                    notifications: NotificationFacade,
                                    templates: TemplateConfiguration,
                                    @Named("rejection-url") rejectionUrl: String,
                                    val authConnector: AuthConnector)
                                   (implicit executionContext: ExecutionContext) extends BackendController(cc) with AuthorisedFunctions {

  private val formatter = {
    val patterns: util.Map[java.lang.Long, String] = new util.HashMap[java.lang.Long, String]()
    patterns.put(0L, "am")
    patterns.put(1L, "pm")
    new DateTimeFormatterBuilder()
      .appendPattern("h:mm")
      .appendText(ChronoField.AMPM_OF_DAY, patterns)
      .appendLiteral(' ')
      .appendPattern("d MMMM YYYY")
      .toFormatter
  }

  val submission: Action[FormNotification] = Action.async(parse.json[FormNotification]) { implicit request =>
    authorised() {
      val notification = request.body
      notifications.sendByEmail(
        templates.submission,
        notification.email,
        Map("form-id" -> notification.id, "submission-time" -> formatter.format(notification.time)),
        Reference(notification.id)
      ).map(result => Ok(result.notificationId.asString))
    }
  }

  val acceptance: Action[FormNotification] = Action.async(parse.json[FormNotification]) { implicit request =>
    authorised() {
      val notification = request.body
      notifications.sendByEmail(
        templates.acceptance,
        notification.email,
        Map(
          "form-id" -> notification.id,
          "acceptance-time" -> formatter.format(notification.time)
        ),
        Reference(notification.id)
      ).map(result => Ok(result.notificationId.asString))
    }
  }

  val rejection: Action[FormNotification] = Action.async(parse.json[FormNotification]) { implicit request =>
    authorised() {
      val notification = request.body
      notifications.sendByEmail(
        templates.rejection,
        notification.email,
        Map(
          "form-id" -> notification.id,
          "rejection-time" -> formatter.format(notification.time),
          "url" -> rejectionUrl
        ),
        Reference(notification.id)
      ).map(result => Ok(result.notificationId.asString))
    }
  }
}
