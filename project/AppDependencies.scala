import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-play-26"         % "0.34.0",
    "uk.gov.hmrc"             %% "govuk-template"            % "5.26.0-play-26",
    "uk.gov.hmrc"             %% "play-ui"                   % "7.32.0-play-26",
    "uk.gov.service.notify"   %  "notifications-java-client" % "3.13.0-RELEASE"
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.0.4"                 % "test",
    "org.mockito"             %% "mockito-scala"            % "1.0.8"                 % "test",
    "org.jsoup"               %  "jsoup"                    % "1.10.2"                % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "uk.gov.hmrc"             %% "service-integration-test" % "0.4.0-play-26"         % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "3.1.0"                 % "test, it"
  )

}
