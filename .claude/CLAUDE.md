# AiSolver2048 (2048 Coach) - Claude Code Configuration

Android app that lets a user model arbitrary 2048 board states and asks an on-device AI model to recommend the best next move. Distributed via the Google Play Store as "2048 Coach".

## Overview

- Multi-module Android application: `:app` (entry point), `:ui`, `:data`, `:core` (shared library modules)
- On-device inference using LiteRT (Google AI Edge / TensorFlow Lite)
- Target users: End-users via Google Play Store

### Key Concepts

- **On-device Inference**: Move recommendations come from a `.tflite` model executed locally via LiteRT
- **DataState**: Wrapper for streaming data states (Loading, Loaded, Pending, Error, NoNetwork)
- **Result Types**: Custom sealed classes for operation results (never throw exceptions from data layer)
- **UDF (Unidirectional Data Flow)**: State flows down, actions flow up through ViewModels

---

## Architecture

```
User Request (UI Action)
         |
    Screen (Compose)
         |
    ViewModel (State/Action/Event)
         |
    Repository (Business Logic)
         |
    +----+----+
    |         |
  Disk      LiteRT
   |          |
SharedPrefs .tflite
```

### Key Principles

1. **No Exceptions from Data Layer**: All suspending functions return `Result<T>` or custom sealed classes
2. **State Hoisting to ViewModel**: All state that affects behavior must live in the ViewModel's state
3. **Interface-Based DI**: All implementations use interface/`...Impl` pairs with Hilt injection

### Core Patterns

- **BaseViewModel**: Enforces UDF with State/Action/Event pattern. See `ui/src/main/java/io/github/helpigstar/aisolver2048/ui/platform/base/BaseViewModel.kt`.
- **Repository Result Pattern**: Type-safe error handling using custom sealed classes for discrete operations and `DataState<T>` wrapper for streaming data.
- **Common Patterns**: Flow collection via `Internal` actions, error handling via `when` branches, `DataState` streaming with `.map { }` and `.stateIn()`.

> For complete architecture patterns, code templates, and module organization, see `docs/ARCHITECTURE.md`.

---

## Development Guide

### Workflow Skills

> **Quick start**: Use the `android-architect` agent (or `/plan-android-work <task>`) to refine requirements and plan,
> then the `android-implementer` agent (or `/work-on-android <task>`) for implementation,
> then `/review-android <PR#>` to review the result.

Planning: 1–2 | Implementation: 3–5 | Review & PR: 6–8

1. `refining-android-requirements` - Gap analysis and structured spec from any input source
2. `planning-android-implementation` - Architecture design and phased task breakdown
3. `implementing-android-code` - Patterns, gotchas, and templates for writing code
4. `perform-android-preflight-checklist` - Quality gate before committing
5. `committing-android-changes` - Commit message format and pre-commit workflow
6. `reviewing-changes` - Android-specific MVVM/Compose code review checklists (invoked by `/review-android`)
7. `/review-android` - Full review workflow: PR context gathering → Android checklist → output
8. `creating-android-pull-request` - PR creation workflow and templates

---

## Code Style & Standards

- **Formatter**: Android Studio with `bitwarden-style.xml` | **Line Limit**: 100 chars
- **Naming**: `camelCase` (vars/fns), `PascalCase` (classes), `SCREAMING_SNAKE_CASE` (constants), `...Impl` (implementations)
- **KDoc**: Required for all public APIs
- **String Resources**: Add new strings to `:ui` module (`ui/src/main/res/values/strings.xml`). Use typographic quotes/apostrophes (`"` `"` `'`) not escaped ASCII (`\"` `\'`)

> For complete style rules (imports, formatting, documentation, Compose conventions), see `docs/STYLE_AND_BEST_PRACTICES.md`.

---

## Anti-Patterns

In addition to the Key Principles above, follow these rules:

### DO
- Map async results to internal actions before updating state
- Inject `Clock` for time-dependent operations
- Return early to reduce nesting

### DON'T
- Update state directly inside coroutines (use internal actions)
- Use `any` types or suppress null safety
- Catch generic `Exception` (catch specific types)
- Use `e.printStackTrace()` (use Timber logging)
- Create new patterns when established ones exist
- Skip KDoc for public APIs

---

## Quick Reference

- **Code style**: Full rules: `docs/STYLE_AND_BEST_PRACTICES.md`
- **Before writing code**: Use `implementing-android-code` skill for project-specific patterns, gotchas, and templates
- **Before committing**: Use `perform-android-preflight-checklist` skill, then `committing-android-changes` skill for message format
- **Code review**: Use `/review-android` for the full review workflow; `reviewing-changes` skill for checklist-only
- **Creating PRs**: Use `creating-android-pull-request` skill for PR workflow and templates
- **Troubleshooting**: See `docs/TROUBLESHOOTING.md`
- **Architecture**: `docs/ARCHITECTURE.md` | [Jetpack Compose](https://developer.android.com/jetpack/compose) | [Hilt DI](https://dagger.dev/hilt/) | [LiteRT](https://ai.google.dev/edge/litert)
