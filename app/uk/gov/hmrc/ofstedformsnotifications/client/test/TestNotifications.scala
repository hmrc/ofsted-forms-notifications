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

package uk.gov.hmrc.ofstedformsnotifications.client.test

import java.util.UUID

import uk.gov.hmrc.ofstedformsnotifications.client._

import scala.concurrent.Future

class TestNotifications extends NotificationFacade {
  override def sendByEmail(template: TemplateId,
                           email: Email,
                           personalization: Map[String, Any],
                           reference: Reference): Future[EmailNotification] = {
    email match {
      case Email("ok@example.com") => Future.successful {
        EmailNotification(NotificationId(UUID.randomUUID()), None, template, 0, "", "", "", None)
      }
      case Email("error@example.com") => Future.failed(new Exception("Error in upstream comunication"))
      case _ => Future.failed(new IllegalStateException("Error in internal messeging"))
    }
  }
}
