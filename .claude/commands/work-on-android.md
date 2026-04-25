---
description: Guided Android development workflow through all lifecycle phases
argument-hint: <task description or plan reference>
---

# Android Development Workflow

You are guiding the developer through a complete Android development lifecycle for the AiSolver2048 Android project. The task to work on is:

**Task**: $ARGUMENTS

## Workflow Phases

Work through each phase sequentially. **Confirm with the user before advancing to the next phase.** If a phase fails (lint errors, etc.), loop on that phase until resolved before advancing. The user may skip phases that are not applicable.

### Phase 1: Implement

Invoke `Skill(implementing-android-code)` to guide your implementation of the task. Understand what needs to be done, explore the relevant code, and write the implementation.

**Before advancing**: Summarize what was implemented and confirm the user is ready to move to self-review.

### Phase 2: Self-Review

Invoke `Skill(perform-android-preflight-checklist)` to perform a quality gate check on all changes. Address any issues found.

**Before advancing**: Share the self-review results and confirm readiness to commit.

### Phase 3: Commit

Invoke `Skill(committing-android-changes)` to stage and commit the changes with a properly formatted commit message.

**Before advancing**: Confirm the commit was successful and ask if the user wants to proceed to PR creation, or stop here.

### Phase 4: Pull Request

Prompt the user to invoke `Skill(creating-android-pull-request)` to create the pull request with proper description and formatting. **Create as a draft PR by default** unless the user has explicitly requested a ready-for-review PR.

## Guidelines

- Be explicit about which phase you are in at all times.
- Never proceed to another phase without user confirmation.
- If the user wants to skip a phase, acknowledge and move to the next applicable phase.
- If starting from a partially completed task (e.g., code already written), skip to the appropriate phase.
