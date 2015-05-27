import android.Keys._

scalaVersion := "2.11.6"

incOptions := incOptions.value.withNameHashing(true)

resolvers ++= Seq(
  "Sonatype Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "3.6.1-10",
  "com.mcxiaoke.volley" % "library" % "1.0.15",
  "com.squareup.okhttp" % "okhttp" % "2.3.0",
  "org.jsoup" % "jsoup" % "1.8.2"
)

platformTarget in Android := "android-21"

proguardOptions in Android ++= Seq()

run <<= run in Android

android.Plugin.androidBuild
