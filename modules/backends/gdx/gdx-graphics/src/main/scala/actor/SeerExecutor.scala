
// based on Code from Viktor Klang: https://gist.github.com/viktorklang/2422443

package seer.actor

import akka.dispatch.{ DispatcherPrerequisites, ExecutorServiceFactory, ExecutorServiceConfigurator }
import com.typesafe.config.Config
import java.util.concurrent.{ ExecutorService, AbstractExecutorService, ThreadFactory, TimeUnit }
import java.util.Collections

import com.badlogic.gdx.Gdx

// First we wrap invokeLater as a ExecutorService
object SeerExecutorService extends AbstractExecutorService { 
  def execute(command: Runnable) = Gdx.app.postRunnable(command)
  def shutdown(): Unit = ()
  def shutdownNow() = Collections.emptyList[Runnable]
  def isShutdown = false
  def isTerminated = false
  def awaitTermination(l: Long, timeUnit: TimeUnit) = true
}

// Then we create an ExecutorServiceConfigurator so that Akka can use our SwingExecutorService for the dispatchers
class SeerEventThreadExecutorServiceConfigurator(config: Config, prerequisites: DispatcherPrerequisites) extends ExecutorServiceConfigurator(config, prerequisites) {
  private val f = new ExecutorServiceFactory { def createExecutorService: ExecutorService = SeerExecutorService }
  def createExecutorServiceFactory(id: String, threadFactory: ThreadFactory): ExecutorServiceFactory = f
}

// Then we simply need to create a dispatcher configuration in our application.conf
// seer-dispatcher {
//   type = "Dispatcher"
//   executor = "seer.actor.SeerEventThreadExecutorServiceConfigurator"
//   throughput = 1
// }

// After that we just create the GUI Actors with a Props with the correct dispatcher set:
// val frameActor = context.actorOf(Props[FrameActor].withDispatcher("swing-dispatcher"), "frame-actor")