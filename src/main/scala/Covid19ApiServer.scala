package com.dora
import cats.effect.{Async, IO}
import cats.syntax.all.*
import com.comcast.ip4s.*
import com.dora.routes.Covid19Routes
import com.dora.services.Covid19ApiService
import org.http4s.Uri
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.Origin
import org.http4s.implicits.*
import org.http4s.server.middleware.{CORS, Logger}

import scala.language.postfixOps

object Covid19ApiServer:

  private val service = new Covid19ApiService
  private val httpApp = (Covid19Routes.covid19Routes(service) <+> Covid19Routes.maxMinRoutes(service)).orNotFound
  private val finalHttpApp = Logger.httpApp(true, true)(httpApp)

  def run: IO[Unit] = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp(finalHttpApp)
    .build
    .useForever
