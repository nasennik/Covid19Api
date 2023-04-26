package com.dora
package routes

import cats.effect.unsafe.IORuntime
import cats.effect.{IO, Sync}
import com.dora.services.Covid19ApiService
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.implicits.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import com.comcast.ip4s.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.dsl.io.{/, GET, Ok, Root}
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Covid19Routes {
  
private val logger =Slf4jLogger.getLogger[IO]
  private object FromDateQueryParamMatcher extends QueryParamDecoderMatcher[String]("from")

  private object ToDateQueryParamMatcher extends QueryParamDecoderMatcher[String]("to")
  
  def countriesRoute(service: Covid19ApiService):
   HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root/ "covid19"/ "countries" =>
      for {
        countryList <- service.getCountryList
        resp <- Ok(countryList)
      } yield resp
  }
//http://localhost:8080/covid19/country/belarus?from=2022-01-01&to=2022-12-31
  def covid19Routes(service: Covid19ApiService):
  HttpRoutes[IO] = HttpRoutes.of[IO] {
        
    case GET -> Root / "covid19" / "country" / country :?
      FromDateQueryParamMatcher(from) +& ToDateQueryParamMatcher(to) =>
      for {
        covid19Cases <- service.getCovidCases(country, from, to)
        resp <- Ok(covid19Cases)
      } yield resp
  }


  def maxMinRoutes(service: Covid19ApiService):
  HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / "covid19"/ "maxMin" / "country" / country :?
      FromDateQueryParamMatcher(from) +& ToDateQueryParamMatcher(to)  =>
      for {
        maxMinCases <- service.getMaxMinCases(country, from, to)
        resp <- Ok(maxMinCases)
      } yield resp
  }
}
