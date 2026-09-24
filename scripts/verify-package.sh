#!/usr/bin/env bash
set -euo pipefail
sdk_root=$(cd "$(dirname "$0")/.." && pwd)
standalone=$(mktemp -d)
trap 'rm -rf "$standalone"' EXIT
# No Git metadata, monorepo files, existing build output, or release credentials.
(cd "$sdk_root" && tar -cf - build.gradle settings.gradle gradle.properties gradle.lockfile gradlew gradlew.bat gradle src README.md LICENSE) | (cd "$standalone" && tar -xf -)
cd "$standalone"
./gradlew --no-daemon check jar sourcesJar plainJavadocJar generatePomFileForMavenPublication
