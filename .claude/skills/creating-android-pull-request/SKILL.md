---
name: creating-android-pull-request
version: 0.1.0
description: Pull request creation workflow for AiSolver2048 Android. Use when creating PRs, writing PR descriptions, or preparing branches for review. Triggered by "create PR", "pull request", "open PR", "gh pr create", "PR description".
---

# Create Pull Request

## PR Title Format

```
<type>: <short imperative summary>
```

**Examples:**
- `feat: Add board input editing for arbitrary 2048 states`
- `fix: Resolve crash when loading the LiteRT model`
- `refactor: Simplify onboarding navigation flow`

**Rules:**
- Keep under 70 characters total
- Use imperative mood in the summary

**Type keywords:**

Invoke the `labeling-android-changes` skill for the full type keyword table and selection guidance.

---

## Pre-PR Checklist

1. **Self-review done**: Use `perform-android-preflight-checklist` skill
2. **No unintended changes**: Check `git diff origin/main...HEAD` for unexpected files
3. **Branch up to date**: Rebase on `main` if needed

---

## Creating the PR

```bash
# Ensure branch is pushed
git push -u origin <branch-name>

# Create PR as draft by default
gh pr create --draft --title "feat: Short summary" --body "<fill in description>"
```

**Default to draft PRs.** Only create a non-draft (ready for review) PR if the user explicitly requests it.

---

## Base Branch

- Default target: `main`
- Check with team if targeting a feature branch instead
