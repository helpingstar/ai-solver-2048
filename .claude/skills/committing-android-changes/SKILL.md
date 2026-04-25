---
name: committing-android-changes
version: 0.1.0
description: Git commit conventions and workflow for AiSolver2048 Android. Use when committing code, writing commit messages, or preparing changes for commit. Triggered by "commit", "git commit", "commit message", "prepare commit", "stage changes".
---

# Git Commit Conventions

## Commit Message Format

```
<type>: <imperative summary>

<optional body explaining why, not what>
```

### Rules

1. **Type keyword**: Include a conventional commit type (see table below)
2. **Imperative mood**: "Add feature" not "Added feature" or "Adds feature"
3. **Short summary**: Under 72 characters for the first line
4. **Body**: Explain the "why" not the "what" — the diff shows the what

### Type Keywords

Invoke the `labeling-android-changes` skill for the full type keyword table and selection guidance.

### Example

```
feat: Add board input editing for arbitrary 2048 states

Users want to model mid-game positions before asking the coach for a
recommendation. This adds an editable cell grid backed by the workspace state.
```

### Followup Commits

Only the first commit on a branch needs the full format (type keyword, body). Subsequent commits — whether addressing review feedback, making intermediate changes, or iterating locally — can use a short, descriptive summary with no prefix or body required.

```
Update error handling in inference flow
```

---

## Pre-Commit Checklist

Run the `perform-android-preflight-checklist` skill for the full quality gate. At minimum, before staging and committing:

1. **Review staged changes**: `git diff --staged` — verify no unintended modifications
2. **Verify no secrets**: No API keys, tokens, passwords, or `.env` files staged
3. **Verify no generated files**: No build outputs, `.idea/` changes, or generated code

---

## What NOT to Commit

- `.env` files or `user.properties` with real tokens
- Credential files or signing keystores
- Build outputs (`build/`, `*.apk`, `*.aab`)
- IDE-specific files (`.idea/` changes, `*.iml`)
- Large binary files

---

## Staging Best Practices

- **Stage specific files** by name rather than `git add -A` or `git add .`
- Put each file path on its own line for readability:
  ```bash
  git add \
    path/to/first/File.kt \
    path/to/second/File.kt \
    path/to/third/File.kt
  ```
- Review each file being staged to avoid accidentally including sensitive data
- Use `git status` (without `-uall` flag) to see the working tree state
