# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to the plugin versioning scheme `1.0+<paper-version>`
(for example, `1.0+26.3` targets Paper / Minecraft 26.3).

> **Note:** As of Paper 26.1, Minecraft and Paper use year-based versioning
> (`<year>.<drop>[.<patch>]`, e.g. `26.1`, `26.3`). The old `1.21.x` style does
> not apply to these releases.

## [Unreleased]

## [1.0+26.3] - 2026-11-02

### Changed

- Update Paper API dependency from `26.2.build.+` to `26.3.build.+`
- Update `minecraft` version from `26.2` to `26.3`
- Update `api-version` in `paper-plugin.yml` from `26.2` to `26.3`
- Update README server requirement from `Paper 1.21.11+` to `Paper 26.3+`
- Fix placeholder `your-repo` release link in the README

### Added

- `CHANGELOG.md` (this file) — project now follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)

### Verified

- Compiles against the Paper dev bundle `26.3.build.+` (resolved `26.3.build.237-stable`)
- All runtime reflection targets confirmed against the Paper 26.3 mapped sources:
  - `MerchantMenu.trader`
  - `MerchantMenu.tradeContainer`
  - `Villager.updateSpecialPrices(Player)`
  - `MerchantContainer.activeOffer`
  - `AbstractVillager.resetOffers()` (Paper-added method, reached via smart-cast)
- Boot-tested on a real server — Paper 26.3 build 237: plugin loads, enables, and reaches
  `Done` with no exceptions; companion-object reflection init succeeds at class-load time

### Compatibility Notes

- Requires **Java 25** (required by Paper 26.3; verified from the test server bootstrap log)
- Requires **Paper 26.3+** (not compatible with Spigot/CraftBukkit, as before)
- Client-side [Trade Cycling mod](https://modrinth.com/mod/trade-cycling) compatibility is
  unchanged: the `trade_cycling:cycle_trades` packet identifier is identical in mod
  versions 26.2 and 26.3
- **No code changes were required** — the 26.2 → 26.3 jump is a pure version-target bump

## [1.0+26.2] - 2026-04-26

Initial tracked release state of this fork line (already present upstream):

- Targets Paper `26.2.build.+`, `api-version: '26.2'`
- Intercepts the `trade_cycling:cycle_trades` custom payload and refreshes villager
  trades via reflection (`MerchantMenu.trader`, `MerchantMenu.tradeContainer`,
  `Villager.updateSpecialPrices(Player)`)

[1.0+26.3]: https://github.com/Sheepearrrrrrrrrr/TradeCyclingPaper/compare/master...update/paper-26.3
[1.0+26.2]: https://github.com/Sheepearrrrrrrrrr/TradeCyclingPaper/releases
