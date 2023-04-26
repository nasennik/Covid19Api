package com.dora
package models

import cats.effect.Concurrent
import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

import java.time.ZonedDateTime

case class Covid19Cases(country: String, cases: Int, date: ZonedDateTime) 

object Covid19Cases {
  given Decoder[Covid19Cases] = Decoder.instance { h =>
    for {
      country <- h.get[String]("Country")
      cases <- h.get[Int]("Cases")
      date <- h.get[ZonedDateTime]("Date")
    } yield Covid19Cases(country, cases, date)
  }

  given[F[_] : Concurrent]: EntityDecoder[F, Covid19Cases] = jsonOf

  given Encoder[Covid19Cases] = Encoder.AsObject.derived[Covid19Cases]

  given[F[_]]: EntityEncoder[F, Covid19Cases] = jsonEncoderOf

  given Decoder[List[Covid19Cases]] = Decoder.decodeList[Covid19Cases]

  given[F[_] : Concurrent]: EntityDecoder[F, List[Covid19Cases]] = jsonOf

  given Encoder[List[Covid19Cases]] = Encoder.encodeList[Covid19Cases]

  given[F[_]]: EntityEncoder[F, List[Covid19Cases]] = jsonEncoderOf
}

