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

import java.time.format.DateTimeFormatter

import javax.inject.{Inject, Named, Singleton}
import play.api.libs.json.JsError
import play.api.mvc._
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.ofstedformsnotifications.client.{NotificationFacade, Reference}
import uk.gov.hmrc.ofstedformsnotifications.{FormNotification, TemplateConfiguration}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OfstedNotifications @Inject()(mcc: MessagesControllerComponents,
                                    notifications: NotificationFacade,
                                    templates: TemplateConfiguration,
                                    @Named("rejection-url") rejectionUrl: String,
                                    val authConnector: AuthConnector)
                                   (implicit executionContext: ExecutionContext) extends FrontendController(mcc) with AuthorisedFunctions {

  private val formatter = DateTimeFormatter.ofPattern("hh:mm a dd-MMMM-yyyy")

  val submission = Action.async(parse.json) { implicit request =>
    authorised() {
      request.body.validate[FormNotification].fold(
        err => Future.successful(BadRequest(JsError.toJson(JsError(err)))),
        notification => notifications.sendByEmail(
          templates.submission,
          notification.email,
          Map("form-id" -> notification.id, "submission-time" -> formatter.format(notification.time)),
          Reference(notification.id)
        ).map(result => Ok(result.notificationId.asString))
      )
    }
  }

  val acceptance = Action.async(parse.json) { implicit request =>
    authorised() {
      request.body.validate[FormNotification].fold(
        err => Future.successful(BadRequest(JsError.toJson(JsError(err)))),
        notification => notifications.sendByEmail(
          templates.acceptance,
          notification.email,
          Map(
            "form-id" -> notification.id,
            "acceptance-time" -> formatter.format(notification.time)
          ),
          Reference(notification.id)
        ).map(result => Ok(result.notificationId.asString))
      )
    }
  }

  val rejection = Action.async(parse.json) { implicit request =>
    authorised() {
      request.body.validate[FormNotification].fold(
        err => Future.successful(BadRequest(JsError.toJson(JsError(err)))),
        notification => notifications.sendByEmail(
          templates.rejection,
          notification.email,
          Map(
            "form-id" -> notification.id,
            "rejection-time" -> formatter.format(notification.time),
            "url" -> rejectionUrl
          ),
          Reference(notification.id)
        ).map(result => Ok(result.notificationId.asString))
      )
    }
  }


}
