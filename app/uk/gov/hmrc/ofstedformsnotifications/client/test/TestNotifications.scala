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
      case _ => Future.failed(new IllegalStateException("Error in upstream comunication"))
    }
  }
}
