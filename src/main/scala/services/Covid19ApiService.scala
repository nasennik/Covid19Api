package com.dora
package services

import cats.Monad
import cats.effect.{Concurrent, IO}
import com.dora.models.{Country, Covid19Cases, MaxMinCases}
import io.circe.Json
import org.http4s.Uri.unsafeFromString
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.dsl.io.GET
import org.http4s.{HttpRoutes, QueryParamDecoder, Request, Response}
import org.http4s.ember.client.{EmberClient, EmberClientBuilder}
import org.http4s.implicits.uri
import cats.implicits.catsSyntaxMonadError
import cats.syntax.all.catsSyntaxMonadError
import cats.syntax.monadError.catsSyntaxMonadError

import java.time.ZonedDateTime
import io.circe.{Decoder, Encoder}
import org.http4s
import org.http4s.*
import org.http4s.Method.*
import org.http4s.UriTemplate.{ParamElm, PathElm}
import org.http4s.circe.*
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits.*
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jLogger
import sun.net.www.http.HttpClient

import java.time.ZonedDateTime
import scala.language.postfixOps

class Covid19ApiService:

  private val logger =Slf4jLogger.getLogger[IO]
  private final case class CovidApiError(e: Throwable) extends RuntimeException

  private val baseUrl = uri"https://api.covid19api.com"
  private val httpClient: Client[IO] = JavaNetClientBuilder[IO].create

   def getCountryList: IO[List[Country]] = {

    val uri = baseUrl / "countries"

      httpClient.expect[List[Country]](Request[IO](uri = uri))
      .adaptError { case t =>
        t.printStackTrace()
        CovidApiError(t)
      }
  }

   def getCovidCases(country: String, from: String, to: String): IO[List[Covid19Cases]] = {
     val uri = baseUrl / "country" / country /
     "status" / "confirmed" +?
       ("from", from) +?
       ("to", to)
     logger.info("stating get covid cases in service")

     httpClient.expect[List[Covid19Cases]](Request[IO](uri = uri))
       .adaptError { case t =>
         t.printStackTrace()
         CovidApiError(t)
       }
   }
  
  def getMaxMinCases(country: String, from: String, to: String): IO[MaxMinCases] = {
  for {
    cases <- getCovidCases(country, from, to)
    groupedByDate = cases.groupBy(_.date.toLocalDate)
    maxMinCases = groupedByDate.view.mapValues { case casesByDate =>
      val maxCases = casesByDate.map(_.cases).max
      val minCases = casesByDate.map(_.cases).min
      MaxMinCases(country, maxCases, minCases, casesByDate.head.date)
    }.values
    maxCases = maxMinCases.map(_.maxCases).max
    minCases = maxMinCases.map(_.minCases).min
    maxMinCasesByDate = maxMinCases.find(_.maxCases == maxCases).get
  } yield MaxMinCases(country, maxCases, minCases, maxMinCasesByDate.date)
  }

