package com.radiance.scala.tonclient.types.in

import io.circe._
import io.circe.derivation._

case class DeploySet(
                      /**
                       * Content of TVC file encoded in `base64`.
                       */
                      tvc: String,

                      /**
                       * Target workchain for destination address. Default is `0`.
                       */
                      workchain_id: Long,

                      /**
                       * List of initial values for contract's public variables.
                       */
                      initial_data: String
                    )

object DeploySet {
  implicit val deploySetEncoder: Encoder[DeploySet] = deriveEncoder[DeploySet]
}