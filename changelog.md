# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

* * *

## [Unreleased]

### Added

- AJAX input/select bind attributes and automatic, guarded `bx-ui-compat` runtime imports for AJAX forms.
- Select column/selection metadata for the compatibility scanner and a `bx-ui-compat` dependency.
- CF rendering and Vitest coverage for bind scanning, source changes, and `ColdFusion.bindHandlerCache`.

### Fixed

- Default form/select IDs to their names and form submission methods to `post` when an action is supplied.
- Preserve lowercase input bind callbacks and options instead of rejecting or dropping them.
- Enable the typeahead attribute-rendering test with assertions matching its input markup.

## [1.2.1] - 2026-08-12

## [1.2.0] - 2026-04-30

### Fixed

- Remove incorrect mapping that was causing lookup/compile conflict

## [1.1.0] - 2025-02-18

### Added

- Updated build scripts and gradle plugins

### Changed

- Removed LoggerFactory usage

## [1.0.0] - 2024-12-04

- First iteration of this module

[unreleased]: https://github.com/ortus-boxlang/bx-ui-forms/compare/v1.2.1...HEAD
[1.2.1]: https://github.com/ortus-boxlang/bx-ui-forms/compare/v1.2.0...v1.2.1
[1.2.0]: https://github.com/ortus-boxlang/bx-ui-forms/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/ortus-boxlang/bx-ui-forms/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/ortus-boxlang/bx-ui-forms/compare/4826defe119f767771053e2896f18c0b825d6de7...v1.0.0
