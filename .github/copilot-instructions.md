# Copilot instructions for ziota2

This file tells future Copilot sessions how this repository is built, tested, and organized so suggestions stay accurate.

---

## Build, test, and lint commands

- Build all modules: `sbt compile`
- Run full test suite: `sbt test` (CI runs `sbt test`)
- Run a single test class or spec:
  - Whole project: `sbt "testOnly fully.qualified.TestClassName"`
  - With wildcard: `sbt "testOnly *MySpec"`
  - Per-subproject (example `common`): `sbt "common/testOnly *MySpec"`
- Run main entrypoint (per README): `sbt "runMain MainProg"`
- Package (native packager): `sbt package` or use native packager tasks like `sbt nativePackager:packageBin`
- Coverage / reporting: scoverage plugin is installed; use `sbt coverage test` and other scoverage tasks from `project/plugins.sbt`
- Lint / rewrite:
  - Scalafix is configured via plugin. Typical tasks: `sbt scalafix` or `sbt scalafixAll`

Notes about tests:
- Tests are configured with `Test / parallelExecution := false` and `Test / fork := true` in build.sbt — tests run serially and are forked. Some tests use Testcontainers and WireMock; CI caches/loading Docker images.

---

## High-level architecture (big picture)
 
- Write code in Scala unless specified otherwise
- Multi-project sbt build. Root aggregates a `module/common` project and depends on it:
  - Root project: top-level application and aggregation
  - module/common: shared code
- Runtime stack:
  - ZIO (core effect system) and ZIO ecosystem libraries (zio-http, zio-json, zio-cli)
  - Quill (quill-jdbc-zio) + HikariCP for database access
  - Flyway for DB migrations
  - JSON via json4s (also present)
- DB targets supported: H2 (local/dev) and PostgreSQL (production/test). Flyway + Quill + Hikari are primary DB layers.
- Tests rely on Testcontainers, WireMock, and cached Docker images in CI for external dependencies (Kafka and WireMock images included in CI workflow).
- CI: GitHub Actions workflow at `.github/workflows/ci.yaml` — it sets up sbt and runs `sbt test`. The workflow also caches Docker images for faster integration tests.

---

## Key conventions and repo-specific patterns

- Function naming rule: repository includes a code-style rule that functions should begin with a verb in present tense. Follow this when adding helpers or public methods.
- ZIO-first codebase: prefer ZIO idioms (ZIO effects, Layers, zio-json) across modules. Tests use `zio-test` and are wired to the ZIO test framework in sbt.
- Database setup: Flyway manages schema; Quill handles mapping. Integration tests may expect a running H2 server (README shows starting H2) or Testcontainers-driven DB instances.
- Tests are deliberately serial/forked — avoid parallelizing test runs when replicating CI locally.
- Plugins and tooling:
  - scoverage, jacoco, sbt-native-packager, sbt-release are configured in `project/plugins.sbt`.
  - scalafix is installed; run `scalafix` tasks before commits if performing automated rewrites or rule fixes.
- Running per-subproject tasks: use the sbt syntax `sbt "<projectId>/test"` or `sbt "<projectId>/testOnly ..."` when targeting module-specific operations.
- Add project dependencies to `build.sbt`'s `mainLib`

---

## Where to look for more context

- README.md — quick start and `runMain MainProg` example
- `build.sbt`, `project/plugins.sbt`, `version.sbt` — build settings, scalaVersion, plugins (scalafix, scoverage, native packager)
- `.github/workflows/ci.yaml` — CI steps and Docker-image caching
- GEMINI.md — repository-specific notes present in the repo

---

If a Copilot session needs to make structural edits (adding entrypoints, new modules, or CI changes) prefer small, focused changes and run `sbt test` locally (or the same CI job) to verify.

