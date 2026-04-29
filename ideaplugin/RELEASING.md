# Releasing the IntelliJ plugin

Single-maintainer, workstation-based release flow. Credentials live in
`~/.gradle/gradle.properties`; the version + changelog ritual is automated
by `net.researchgate.release` and `org.jetbrains.changelog`.

## One-time setup

### 1. JetBrains Marketplace token

1. Sign in at https://plugins.jetbrains.com.
2. Profile → **My Tokens** → create a permanent token (descriptive name like
   `viritin-color-release-mac`).

### 2. Generate a signing certificate

Pick a directory outside the repo (e.g. `~/.viritin-color-cert/`) and create
a 4096-bit RSA private key plus a long-lived self-signed certificate:

```bash
mkdir -p ~/.viritin-color-cert && cd ~/.viritin-color-cert

openssl genrsa -aes256 -out private.pem 4096
openssl req -key private.pem -new -x509 -days 3650 -out chain.crt \
    -subj "/CN=Viritin/O=Viritin/C=FI"

chmod 600 private.pem chain.crt
```

Back up `private.pem`, `chain.crt`, and the passphrase to a password manager.

### 3. Wire credentials into Gradle

Append to `~/.gradle/gradle.properties` (template at
`ideaplugin/gradle.properties.template`):

```properties
marketplaceToken=<token from step 1>
certificatePath=/Users/you/.viritin-color-cert/chain.crt
privateKeyPath=/Users/you/.viritin-color-cert/private.pem
privateKeyPassword=<passphrase from step 2>
```

`~/.gradle/gradle.properties` is per-machine and outside the repo — never commit it.

## Daily flow: changelog as you work

`CHANGELOG.md` is the single source of truth for `<change-notes>` in the
published plugin descriptor. While developing, add bullets under
`## [Unreleased]` (Keep a Changelog conventions: `### Added`, `### Changed`,
`### Fixed`, `### Removed`). No need to edit `plugin.xml` — the changelog
plugin renders the latest section into the descriptor at build time.

## Cutting a release

```bash
cd ideaplugin/
./gradlew release
```

The release task interactively walks through:
1. Confirms the release version (strips `-SNAPSHOT`, e.g. `0.2.0-SNAPSHOT` → `0.2.0`).
2. Runs `build`, `verifyPlugin`, and `signPlugin`.
3. Runs `patchChangelog` to roll `[Unreleased]` into a new `[0.2.0] - YYYY-MM-DD`
   section (and create a fresh empty `[Unreleased]`).
4. Commits `gradle.properties` + `CHANGELOG.md`, tags as `v0.2.0`, then bumps to the
   next `-SNAPSHOT`, commits, and pushes.
5. Runs `publishPlugin` to upload the signed ZIP to JetBrains Marketplace.

For a non-interactive run (e.g. when scripted):

```bash
./gradlew release \
    -Prelease.useAutomaticVersion=true \
    -Prelease.releaseVersion=0.2.0 \
    -Prelease.newVersion=0.2.1-SNAPSHOT
```

The first upload of a plugin goes through human moderation (1–2 business days).
Subsequent updates of an already-approved plugin auto-publish within minutes.

## Verifying a signed ZIP

```bash
java -jar marketplace-zip-signer-cli.jar verify \
    -in build/distributions/viritin-color-intellij-0.2.0-signed.zip \
    -cert ~/.viritin-color-cert/chain.crt
```

`marketplace-zip-signer-cli.jar` is downloadable from
https://github.com/JetBrains/marketplace-zip-signer/releases.

## Recovery scenarios

- **Lost the private key** — generate a new pair (step 2) and update
  `~/.gradle/gradle.properties`. Past releases stay verifiable with the
  archived `chain.crt`; future releases use the new chain.
- **Token leaked** — revoke at https://plugins.jetbrains.com → My Tokens,
  generate a new one, update the property.
- **Releasing from a second machine** — copy `~/.viritin-color-cert/` plus
  the four properties; the cert is the publisher identity and is fine to
  share between your own machines.
- **Aborted release** — if `./gradlew release` fails partway, check
  `git status` for uncommitted version/changelog edits and revert them
  with `git checkout gradle.properties CHANGELOG.md` before retrying.
