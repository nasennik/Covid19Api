package com.dora

import services.Covid19ApiService

import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  val run = Covid19ApiServer.run
}
