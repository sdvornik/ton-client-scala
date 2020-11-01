package com.radiance.scala.tonclient

import java.io._

import com.radiance.scala.tonclient.crypto.Crypto
import com.radiance.tonclient.TONContext
import io.circe.Json
import io.circe._
import io.circe.parser._

import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.Success
import io.circe.syntax._

object TonContext {

  private var requestCount = 0
  private val responses =  mutable.Map[Int, Promise[String]]()

  try {
    System.load(createTempDll("/tonclient.dll"))
  } catch {
    case e: IOException =>
      throw new RuntimeException(e)
  }

  @throws[IOException]
  private def createTempDll(fileName: String) = {
    val inputStream = classOf[TonContext].getResourceAsStream(fileName)
    if (inputStream == null) throw new IOException("Cannot find resource '" + fileName + "'")
    val tempDll = File.createTempFile("TONLibrary", ".dll")
    val outputStream = new FileOutputStream(tempDll)
    val array = new Array[Byte](8192)
    var i = inputStream.read(array)
    while ( { i != -1 }) {
      outputStream.write(array, 0, i)
      i = inputStream.read(array)
    }
    inputStream.close()
    outputStream.close()
    tempDll.deleteOnExit()
    tempDll.getAbsolutePath
  }

  def responseHandler(id: Int, params: String, `type`: Int, finished: Boolean): Unit = {
    this.synchronized({
        responses.remove(id).map(promise => {
          if (`type` == 1) {
            val cursor = parse(params).getOrElse(Json.Null).hcursor
            val code: Int = cursor.downField("code").as[Int].getOrElse(-1)
            val message = cursor.downField("message").as[String].getOrElse("")
            promise.failure(new RuntimeException(s"Code: $code; Message: $message"))
          } else {
            promise.complete(Success(params))
          }
        })
      }
    )
  }

  @throws[Exception]
  def create(config: String): TonContext = {
    val result = TONContext.createContext(config)
    val cursor = parse(result).getOrElse(Json.Null).hcursor
    cursor.downField("error").success.map(e => {
      val code = e.downField("code").as[Int]
      val message = e.downField("message").as[String]
      throw new RuntimeException(s"Code: $code; Message: $message")
    }).getOrElse({
      cursor.downField("result").as[Int].map(i => new TonContext(i)).fold(
        t => throw new RuntimeException(t),
        r => r
      )
    })
  }

  def main(args: Array[String]): Unit = {
    val ctx = TonContext.create("{}")
    val crypto = new Crypto(ctx)(ExecutionContext.global)
    //println(util.Arrays.asList(crypto.factorize("EE").get))
    println(Await.result(ctx.request("client.version", ""), 1.second))
  }
}

class TonContext private(var contextId: Int) {
  implicit val ec: ExecutionContext = ExecutionContext.global

  def destroy(): Unit = {
    if (contextId >= 0) {
      TONContext.destroyContext(contextId)
      contextId = -1
    }
  }

  def request(functionName: String, params: String): Future[String] = {
    val promise: Promise[String] = Promise[String]()
    this.synchronized({
      TonContext.requestCount += 1
      val id = TonContext.requestCount
      TonContext.responses.put(id, promise)
      TONContext.request(contextId, functionName, params, id)
    })
    promise.future
  }

  def exec[In <: Api : Encoder](arg: In): Future[Either[Throwable, arg.Out]] =
    request(arg.functionName, arg.asJson.noSpaces).map(r => parse(r).fold(
      t => Left(t),
      a => arg.fieldName.map(f => a.hcursor.get[arg.Out](f)(arg.decoder)).getOrElse(a.as[arg.Out](arg.decoder))
    )).recover(t => Left(t))
}