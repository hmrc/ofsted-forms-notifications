package uk.gov.hmrc.ofstedformsnotifications.client

import java.net.{Authenticator, PasswordAuthentication}

class ProxyAuthenticator(username: String, password: Array[Char]) extends Authenticator {
  override val getPasswordAuthentication: PasswordAuthentication = new PasswordAuthentication(username, password)
}
