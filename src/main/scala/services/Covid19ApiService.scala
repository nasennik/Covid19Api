package com.dora
package services

import models.*

import cats.Monad
import cats.effect.{Concurrent, IO}
import cats.implicits.catsSyntaxMonadError
import cats.syntax.all.catsSyntaxMonadError
import cats.syntax.monadError.catsSyntaxMonadError
import io.circe.{Decoder, Encoder, Json}
import org.http4s
import org.http4s.Method.*
import org.http4s.Uri.unsafeFromString
import org.http4s.UriTemplate.{ParamElm, PathElm}
import org.http4s.circe.*
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.dsl.io.GET
import org.http4s.ember.client.{EmberClient, EmberClientBuilder}
import org.http4s.implicits.*
import org.http4s.*
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jLogger
import sun.net.www.http.HttpClient
import cats.effect.unsafe.implicits.global

import java.time.{LocalDate, ZonedDateTime}
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
     val previousDate = LocalDate.parse(from).minusDays(1).toString
     val uri = baseUrl / "country" / country /
     "status" / "confirmed" +?
       ("from", previousDate) +?
       ("to", to)
     logger.info("stating get covid cases in service")

     httpClient.expect[List[Covid19Cases]](Request[IO](uri = uri))
       .adaptError { case t =>
         t.printStackTrace()
         CovidApiError(t)
       }
   }

  import com.dora.models.MaxMinCases
  
  private def calculateMinMaxCases(covidCasesList: List[Covid19Cases]): MaxMinCases = {
  val (maxCases, maxCasesDate, minCases, minCasesDate) = covidCasesList.sliding(2).foldLeft(
    (Int.MinValue, ZonedDateTime.now(), Int.MaxValue, ZonedDateTime.now())) {
    case ((maxCases, maxCasesDate, minCases, minCasesDate), Seq(prevDay, currDay)) =>
      val newCases = currDay.cases - prevDay.cases
      val currDate = currDay.date
      if (newCases > maxCases) {
        (newCases, currDate, minCases, minCasesDate)
      } else if (newCases < minCases) {
        (maxCases, maxCasesDate, newCases, currDate)
      } else {
        (maxCases, maxCasesDate, minCases, minCasesDate)
      }
  }
  MaxMinCases(covidCasesList.head.country, maxCases, maxCasesDate, minCases, minCasesDate)
}
  def getMaxMinCases(country: String, from: String, to: String): IO[MaxMinCases] = {

    for {
      cases <- getCovidCases(country, from, to)
      maxMinCases = calculateMinMaxCases(cases)
    } yield maxMinCases
  }

