import sbt._
import scala.sys.process._
import ReleaseTransformations._


name := "fomantic-slim-default"
organization := "com.github.eikek"
description := "A css only build of fomantic-ui without google-fonts provided as webjar."
licenses := Seq("MIT" -> url("http://spdx.org/licenses/MIT"))

autoScalaLibrary := false
crossPaths := false

Compile / resourceGenerators += Def.task {
  val logger = streams.value.log
  val wd = (Compile / baseDirectory).value
  val dist = buildCssFiles(logger, wd)
  copyWebjarResources(dist, (Compile / resourceManaged).value, name.value, version.value, logger)
}.taskValue


// --- publishing

publishMavenStyle := true
scmInfo := Some(
  ScmInfo(
    url("https://github.com/eikek/fomantic-slim-default.git"),
    "scm:git:git@github.com:eikek/fomantic-slim-default.git"
  )
)
developers := List(
  Developer(
    id = "eikek",
    name = "Eike Kettner",
    url = url("https://github.com/eikek"),
    email = ""
  )
)
homepage := Some(url("https://github.com/eikek/fomantic-slim-default"))
publishTo := sonatypePublishToBundle.value
publishArtifact in Test := false
releaseCrossBuild := false
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)


// --- helpers

def buildCssFiles(logger: Logger, wd: File): Seq[(File, String)] = {
  val proc = Process(Seq((wd/"build.sh").toString), Some(wd))
  logger.info("Compiling CSS files …")
  val out = proc.!!
  logger.info(out)
  val dir = wd/"target"/"dist"
  val paths =
    (dir ** "*.css") +++
    (dir/"themes"/"default" ** "*")

  paths.get.pair(Path.relativeTo(dir))
}

def copyWebjarResources(
    src: Seq[(File, String)],
    base: File,
    artifact: String,
    version: String,
    logger: Logger
): Seq[File] = {
  val targetDir = base / "META-INF" / "resources" / "webjars" / artifact / version
  logger.info(s"Copy webjar resources from ${src.size} files/directories.")
  src.flatMap { case (file, name) =>
    val target = targetDir / name
    IO.createDirectories(Seq(target.getParentFile))
    copyWithGZ(file, target)
  }
}

def copyWithGZ(src: File, target: File): Seq[File] = {
  val gzipFilter = "*.html" || "*.css" || "*.js"
  IO.copy(Seq(src -> target))
  if (gzipFilter.accept(src)) {
    val gz = file(target.toString + ".gz")
    IO.gzip(src, gz)
    Seq(target, gz)
  } else {
    Seq(target)
  }
}
