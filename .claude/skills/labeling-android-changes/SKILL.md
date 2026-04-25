---
name: labeling-android-changes
version: 0.1.0
description: Conventional commit type keywords for PR titles and commit messages. Use when determining the change type for commits or PRs. Triggered by "what type", "label", "change type", "conventional commit".
---

# Labeling Changes

PR titles and commit messages use a conventional commit type keyword.

## Format

```
<type>: <imperative summary>
```

## Type Keywords

| Type | Use for |
|------|---------|
| `feat` | New features or functionality |
| `fix` | Bug fixes |
| `refactor` | Code restructuring without behavior change |
| `chore` | Maintenance, cleanup, minor tweaks |
| `perf` | Performance improvements |
| `docs` | Documentation changes |
| `build` | Build system changes |
| `deps` | Dependency updates |

## Selecting a Type

Infer the type from the task description and changes made. **If the type cannot be confidently determined, ask the user.**