package com.radiance.scala.tonclient.types.both

import io.circe._
import io.circe.derivation._

case class ExecutionOptions(
                             /**
                              * boc with config
                              */
                             blockchain_config: String,

                             /**
                              * time that is used as transaction time
                              */
                             block_time: Long,

                             /**
                              * block logical time
                              */
                             block_lt: Long,
                             transaction_lt: Long
                           )

object ExecutionOptions {
  implicit val executionOptionsCodec: Codec[ExecutionOptions] = deriveCodec[ExecutionOptions]
}