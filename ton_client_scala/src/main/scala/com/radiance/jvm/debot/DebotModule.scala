package com.radiance.jvm.debot

import com.radiance.jvm._
import scala.concurrent.Future

class DebotModule(private val ctx: Context) {

  /**
    * [UNSTABLE](UNSTABLE.md) Executes debot action.
    * Calls debot engine referenced by debot handle to execute input action.
    * Calls Debot Browser Callbacks if needed.
    *
    * # Remarks
    * Chain of actions can be executed if input action generates a list of subactions.@param debot_handle
    * @param action
    */
  def execute(debot_handle: DebotHandle, action: DebotAction): Future[Either[Throwable, Unit]] = {
    val arg = ResultOfAppDebotBrowser.ParamsOfExecute(debot_handle, action)
    ??? // ctx.execAsync("debot.execute", arg)
  }

  /**
    * [UNSTABLE](UNSTABLE.md) Fetches debot from blockchain.
    * Downloads debot smart contract (code and data) from blockchain and creates
    * an instance of Debot Engine for it.
    *
    * # Remarks
    * It does not switch debot to context 0. Browser Callbacks are not called.@param address
    * @param app_object
    */
  def fetch(
      address: String,
      app_object: ParamsOfAppDebotBrowser => ResultOfAppDebotBrowser
  ): Future[Either[Throwable, RegisteredDebot]] = {
    val arg = ResultOfAppDebotBrowser.ParamsOfFetch(address)
    ??? // ctx.execAsync("debot.fetch", arg)
  }

  /**
    * [UNSTABLE](UNSTABLE.md) Destroys debot handle.
    * Removes handle from Client Context and drops debot engine referenced by that handle.@param debot_handle
    */
  def remove(debot_handle: DebotHandle): Future[Either[Throwable, Unit]] = {
    val arg = RegisteredDebot(debot_handle)
    ??? // ctx.execAsyncVoid[RegisteredDebot]("debot.remove", arg)
  }

  /**
    * [UNSTABLE](UNSTABLE.md) Starts an instance of debot.
    * Downloads debot smart contract from blockchain and switches it to
    * context zero.
    * Returns a debot handle which can be used later in `execute` function.
    * This function must be used by Debot Browser to start a dialog with debot.
    * While the function is executing, several Browser Callbacks can be called,
    * since the debot tries to display all actions from the context 0 to the user.
    *
    * # Remarks
    * `start` is equivalent to `fetch` + switch to context 0.@param address
    * @param app_object
    */
  def start(
      address: String,
      app_object: ParamsOfAppDebotBrowser => ResultOfAppDebotBrowser
  ): Future[Either[Throwable, RegisteredDebot]] = {
    val arg = ParamsOfStart(address)
    ??? // ctx.execAsync("debot.start", arg)
  }
}
