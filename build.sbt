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

platformTarget in Android := "android-19"

proguardOptions in Android ++= Seq()

run <<= run in Android

android.Plugin.androidBuild


useProguard in Android := false

useProguardInDebug in Android := false

proguardScala in Android := false

dexMulti in Android := true

dexMinimizeMainFile in Android := true

dexMainFileClasses in Android := Seq(
  "com/example/app/MultidexApplication.class",
  "android/support/multidex/BuildConfig.class",
  "android/support/multidex/MultiDex$V14.class",
  "android/support/multidex/MultiDex$V19.class",
  "android/support/multidex/MultiDex$V4.class",
  "android/support/multidex/MultiDex.class",
  "android/support/multidex/MultiDexApplication.class",
  "android/support/multidex/MultiDexExtractor$1.class",
  "android/support/multidex/MultiDexExtractor.class",
  "android/support/multidex/ZipUtil$CentralDirectory.class",
  "android/support/multidex/ZipUtil.class"
)


apkbuildExcludes in Android ++= Seq(
  "META-INF/MANIFEST.MF",
  "META-INF/LICENSE.txt",
  "META-INF/LICENSE",
  "META-INF/NOTICE.txt",
  "META-INF/NOTICE"
)

javacOptions in Compile ++= Seq("-source", "1.6", "-target", "1.6")
