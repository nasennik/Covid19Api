package com.dora
package models

import cats.effect.Concurrent
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

import java.time.ZonedDateTime

case class MaxMinCases(country: String, maxCases: Int, dateMaxCases: ZonedDateTime, minCases: Int, dateMinCases: ZonedDateTime)

object MaxMinCases:
//
//  implicit val encoder: Encoder[MaxMinCases] = deriveEncoder[MaxMinCases]
//  implicit val decoder: Decoder[MaxMinCases] = deriveDecoder[MaxMinCases]

  given Decoder[MaxMinCases] = Decoder.derived[MaxMinCases]

  given[F[_] : Concurrent]: EntityDecoder[F, List[MaxMinCases]] = jsonOf

  given Encoder[MaxMinCases] = Encoder.AsObject.derived[MaxMinCases]

  given[F[_]]: EntityEncoder[F, MaxMinCases] = jsonEncoderOf


