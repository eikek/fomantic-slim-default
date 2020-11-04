import sbt._
import scala.sys.process._

name := "fomantic-slim-default"
organization := "com.github.eikek"
description := "A css only build of fomantic-ui without google-fonts provided as webjar."

autoScalaLibrary := false
crossPaths := false

Compile / resourceGenerators += Def.task {
  val logger = streams.value.log
  val wd = (Compile / baseDirectory).value
  val dist = buildCssFiles(logger, wd)
  copyWebjarResources(dist, (Compile / resourceManaged).value, name.value, version.value, logger)
}.taskValue

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
