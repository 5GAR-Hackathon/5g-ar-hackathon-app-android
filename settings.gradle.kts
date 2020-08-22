include("app")

pluginManagement {
  val kotlinVersion: String by settings
  resolutionStrategy {
    eachPlugin {
      if (requested.id.namespace == "org.jetbrains.kotlin") useVersion(kotlinVersion)
    }
  }
}