# Fomantic-UI Slim Default

This is a custom build of [Fomantic-UI](https://fomantic-ui.com)
containing only the css excluding the google fonts that is added by
default.

This is packaged as webjar for use in your jvm-based projects.

## Usage

The webjars are pushed to maven-central. With sbt:

``` scala
"com.github.eikek" % "fomantic-slim-default" % "<version>"
```


## Building

If you have [`nix`](https://nixos.org/) installed, run:

- `nix-shell --run 'sbt package'` to build the webjar, or
- `nix-shell --run ./build.sh` to build the css files only

Without nix, install yarn, lessc and yuicompressor manually.
