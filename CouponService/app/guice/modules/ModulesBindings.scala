package guice.modules

import com.google.inject.AbstractModule
import guice.{OnStart, Modules}
import net.codingwell.scalaguice.ScalaModule

class ModulesBindings extends AbstractModule with ScalaModule {
	override def configure(): Unit = {
		bind[OnStart].asEagerSingleton()
		bind[Modules]
	}
}
