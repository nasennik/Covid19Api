package service_test

import cats.effect.IO
import com.dora.services.Covid19ApiService
import org.http4s.client.JavaNetClientBuilder
import org.scalatest.funsuite.AnyFunSuite
import org.http4s.{Request, Uri}
import cats.effect.unsafe.implicits.global
import org.http4s.Status.NotFound
import org.omg.CosNaming.NamingContextPackage.NotFound
import org.http4s.FormDataDecoder.formEntityDecoder
import org.http4s.Uri
import org.http4s.Uri.{RegName, Scheme}


class Covid19ServiceTest extends AnyFunSuite {

  private val covid19ApiService = new Covid19ApiService
  private val httpClient = JavaNetClientBuilder[IO].create

  test("getCountryList should return a non-empty list of countries") {
    val result = covid19ApiService.getCountryList.unsafeRunSync()
    assert(result.nonEmpty)
  }

  test("getCovidCases should return a non-empty list of cases for a valid country and date range") {
    val country = "united-states"
    val from = "2022-01-01"
    val to = "2022-01-07"

    val result = covid19ApiService.getCovidCases(country, from, to).unsafeRunSync()
    assert(result.nonEmpty)
  }

  test("getCovidCases should return an empty list of cases for an invalid country and date range") {
    val country = "invalid-country"
    val from = "2022-01-01"
    val to = "2022-01-07"

    intercept[Throwable] {
      covid19ApiService.getCovidCases(country, from, to).unsafeRunSync()
    }
  }

  test("getCovidCases should throw an exception for an invalid date range") {
    val country = "united-states"
    val from = "2022-01-07"
    val to = "2022-01-01"

    intercept[Throwable] {
      covid19ApiService.getCovidCases(country, from, to).unsafeRunSync()
    }
  }
  //http://localhost:8080/covid19/country/belarus?from=2022-01-01&to=2022-12-31


  test("httpClient should return an error for an invalid URI") {
    val uri = Uri.unsafeFromString("https://invalid-api.com")
    intercept[Throwable] {
      httpClient.expect[String](Request[IO](uri = uri)).unsafeRunSync()
    }
  }
}

