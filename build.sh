#!/usr/bin/env bash

set -e

export NODE_OPTIONS="--max-old-space-size=4096"

rm -rf target/dist
mkdir -p target/dist

yarn install

echo -n "Compiling less to css … "
lessc node_modules/fomantic-ui-less/semantic.less > target/dist/semantic.css
echo "ok."

echo -n "Copying font files … "
mkdir -p target/dist/themes
for dir in $(find node_modules/fomantic-ui-less/ -type d -name "assets"); do
    theme=${dir##node_modules/fomantic-ui-less/themes/}
    mkdir -p "target/dist/themes/$theme"
    cp -r $dir/* "target/dist/themes/$theme/"
done
echo "ok."

echo -n "Minifying css … "
./node_modules/clean-css-cli/bin/cleancss -o target/dist/semantic.min.css target/dist/semantic.css
echo "ok."
