# Review Output Examples

Well-structured code reviews demonstrating appropriate depth, tone, and formatting for different change types.

## Table of Contents

**Format Reference:**
- [Quick Format Reference](#quick-format-reference)
  - [Inline Comment Format](#inline-comment-format-required)
  - [Summary Comment Format](#summary-comment-format)

**Examples:**
- [Example 1: Clean PR (No Issues)](#example-1-clean-pr-no-issues)
- [Example 2: Dependency Update with Breaking Changes](#example-2-dependency-update-with-breaking-changes)
- [Example 3: Feature Addition with Critical Issues](#example-3-feature-addition-with-critical-issues)

**Anti-Patterns:**
- [❌ Anti-Patterns to Avoid](#-anti-patterns-to-avoid)
  - [Problem: Verbose Summary with Multiple Sections](#problem-verbose-summary-with-multiple-sections)
  - [Problem: Praise-Only Inline Comments](#problem-praise-only-inline-comments)
  - [Problem: Missing `<details>` Tags](#problem-missing-details-tags)

**Summary:**
- [Summary](#summary)

---

## Quick Format Reference

### Inline Comment Format (REQUIRED)

**MUST use `<details>` tags.** Only severity + description visible; all other content collapsed.

```
[emoji] **[SEVERITY]**: [One-line issue description]

<details>
<summary>Details and fix</summary>

[Code example or specific fix]

[Rationale explaining why]

Reference: [docs link if applicable]
</details>
```

**Severity Levels:**
- ❌ **CRITICAL** - Blocking, must fix (security, crashes, architecture violations)
- ⚠️ **IMPORTANT** - Should fix (missing tests, quality issues)
- ♻️ **DEBT** - Technical debt (duplication, convention violations, future rework needed)
- 🎨 **SUGGESTED** - Nice to have (refactoring, improvements)
- ❓ **QUESTION** - Seeking clarification (requirements, design decisions)

### Summary Comment Format

Uses the agent's `posting-review-summary` skill format. Surface ❌ CRITICAL issues at the top level for immediate visibility, wrap the full findings list in `<details>` for scannability.

```
**Overall Assessment:** APPROVE / REQUEST CHANGES

[1-2 neutral sentences describing what was reviewed]

**Critical Issues** (if any):
- ❌ [One-line summary with file:line]

<details>
<summary>All findings</summary>

- ❌ **CRITICAL**: [description] (`file:line`)
- ⚠️ **IMPORTANT**: [description] (`file:line`)
- ♻️ **DEBT**: [description] (`file:line`)
- 🎨 **SUGGESTED**: [description] (`file:line`)
- ❓ **QUESTION**: [description] (`file:line`)
</details>
```

For clean PRs with no findings, omit both sections entirely — verdict + 1-2 sentences is sufficient.

**GitHub pitfall**: Never use `#` followed by a number in comment text (e.g., `#42`, `#PR123`). GitHub autolinks these to issues/PRs. Use `Finding 1:` or `item 42` instead.

---

## Example 1: Clean PR (No Issues)

**Context**: Moving shared code to common module, complete migration, all patterns followed

**Review Comment:**
```markdown
**Overall Assessment:** APPROVE

Clean refactoring that moves ExitManager to :ui module, eliminating duplication between apps.
```

**Why this works:**
- Immediate approval visible (2-3 lines)
- One sentence acknowledging the work
- No unnecessary sections or elaborate praise
- Author gets quick feedback and can proceed

---

## Example 2: Dependency Update with Breaking Changes

**Context**: Major version update requiring code migration

**Summary Comment:**
```markdown
**Overall Assessment:** REQUEST CHANGES

**Critical Issues:**
- API migration required for LiteRT 1.x breaking changes (core/inference/LiteRtLoader.kt:34)

See inline comments for migration details.
```

**Inline Comment 1** (on `core/inference/LiteRtLoader.kt:34`):
```markdown
❌ **CRITICAL**: API migration required for LiteRT 1.x

<details>
<summary>Details and fix</summary>

LiteRT 1.x removes the legacy interpreter constructor. All loader sites in this file need migration:

```kotlin
// Current (deprecated)
val interpreter = LegacyInterpreter(modelBuffer)

// Must migrate to
val interpreter = Interpreter(modelBuffer, options)
```

Breaking API change affects:
- All loader call sites
- Test utilities

Consider creating separate PR for this migration given the scope.
</details>
```

**Key Features:**
- Minimal summary (2-3 lines)
- Full details in collapsed inline comment
- Specific file:line references
- Code examples in <details>
- Migration guidance and scope assessment

---

## Example 3: Feature Addition with Critical Issues

**Context**: Implements board input editing for arbitrary 2048 states

**Summary Comment:**
```markdown
**Overall Assessment:** REQUEST CHANGES

**Critical Issues:**
- Exposes mutable state violating MVVM (BoardEditViewModel.kt:78)

See inline comments for all issues and suggestions.
```

**Inline Comment 1** (on `app/board/edit/BoardEditViewModel.kt:78`):
```markdown
❌ **CRITICAL**: Exposes mutable state

<details>
<summary>Details and fix</summary>

Change `MutableStateFlow<State>` to `StateFlow<State>`:

```kotlin
// Current (problematic)
val boardState: MutableStateFlow<BoardState>

// Should be
private val _boardState = MutableStateFlow<BoardState>()
val boardState: StateFlow<BoardState> = _boardState.asStateFlow()
```

Exposing MutableStateFlow allows external mutation, violating MVVM unidirectional data flow.

Reference: docs/ARCHITECTURE.md#mvvm-pattern
</details>
```

**Inline Comment 2** (on `app/board/edit/BoardEditViewModel.kt:92`):
```markdown
⚠️ **IMPORTANT**: Missing error handling test

<details>
<summary>Details and fix</summary>

Add test to prevent regression if error handling changes:

```kotlin
@Test
fun `when invalid cell value entered then returns error state`() = runTest {
    val viewModel = BoardEditViewModel(mockRepository)
    coEvery { mockRepository.validateCell(7) }
        returns Result.failure(InvalidCellValueException())

    viewModel.onCellEdited(0, 0, 7)

    assertEquals(BoardState.Error("Invalid cell value"), viewModel.state.value)
}
```

Ensures error flow remains robust across refactorings.
</details>
```

**Inline Comment 3** (on `app/board/edit/BoardEditScreen.kt:134`):
```markdown
❓ **QUESTION**: Can we use AisolverTextField?

<details>
<summary>Details</summary>

This custom cell input field looks similar to `ui/components/AisolverTextField.kt:67`.

Would using the existing component maintain consistency and reduce custom UI code?
</details>
```

**Key Features:**
- Minimal summary (3-4 lines) with critical issues only
- Each issue gets separate inline comment with `<details>` tag
- Multiple severity levels demonstrated (CRITICAL, IMPORTANT, SUGGESTED, QUESTION)
- Mix of prescriptive fixes and collaborative questions
- Code examples collapsed in <details>
- No "Good Practices" or "Action Items" sections

---

## ❌ Anti-Patterns to Avoid

### Problem: Verbose Summary with Multiple Sections

**What NOT to do:**
```markdown
### Review Complete ✅

## Summary
[Lengthy description of what the PR does]

### Strengths 👍
1. **Excellent documentation** - KDoc comments are comprehensive
2. **Proper fail-closed design** - Security defaults to rejection
3. **Defense in depth** - Multiple validation layers
[7 total items with elaboration]

### Critical Issues ⚠️
- Missing test coverage for security-critical code (with full details)
- [More issues with full explanations]

### Recommendations 🎨
- [Multiple recommendations]

### Test Coverage Status 📊
- [Analysis]

### Architecture Compliance ✅
- [Analysis]

## Recommendation
**Conditional approval** with follow-up...
```

**Why this is wrong:**
- 800+ tokens for a summary comment
- Multiple sections (Strengths, Recommendations, Test Coverage, Architecture)
- Elaborates on positive aspects ("Excellent documentation...")
- Duplicates critical issues (summary has details + inline comments have same details)
- Creates visual clutter in PR conversation

**Correct approach:**
```markdown
**Overall Assessment:** REQUEST CHANGES

**Critical Issues:**
- Missing test coverage for security-critical code (PasswordManagerSignatureVerifierImpl.kt:47)

See inline comments for details.
```

**Key differences:**
- 3-5 lines vs 800+ tokens
- Verdict + critical issues only
- All details belong in inline comments
- No positive commentary sections
- Scales with PR complexity, not analysis thoroughness

### Problem: Praise-Only Inline Comments

**What NOT to do:**

Creating inline comment on `AuthenticatorBridgeManagerImpl.kt:73`:
```markdown
👍 **Excellent integration of signature verification**

The signature verification is properly integrated into the connection flow:
- Checked during initialization (line 73)
- Checked before binding (line 134)
- Ensures only validated apps can connect

This is exactly the right approach for fail-safe security.
```

**Why this is wrong:**
- Entire comment is positive feedback with no actionable issue
- Takes up space in PR conversation
- Distracts from actual issues
- Violates "focus on actionable feedback" principle

**Correct approach:**
- Do not create this comment at all
- Reserve inline comments exclusively for issues requiring attention

### Problem: Missing `<details>` Tags

**What NOT to do:**

```markdown
❌ **CRITICAL**: Missing test coverage for security-critical code

The `@OmitFromCoverage` annotation excludes this entire class from test coverage.

**Problems:**
1. No validation that certificate hashes match the expected certificates
2. No verification of fail-closed behavior on edge cases
3. No tests for multiple signer rejection logic
4. Certificate hash typos would go undetected until production

**Recommendation:**
Replace `@OmitFromCoverage` with proper unit tests.

Example test structure:
[long code block]

Security-critical code should have the highest test coverage, not be omitted.
```

**Why this is wrong:**
- All content visible immediately (code examples, problems list, rationale)
- Creates visual clutter in PR conversation
- Makes it hard to scan multiple issues quickly

**Correct approach:**
```markdown
❌ **CRITICAL**: Missing test coverage for security-critical code

<details>
<summary>Details and fix</summary>

The `@OmitFromCoverage` annotation excludes this entire class from test coverage.

**Problems:**
1. No validation that certificate hashes match the expected certificates
2. No verification of fail-closed behavior on edge cases
3. No tests for multiple signer rejection logic
4. Certificate hash typos would go undetected until production

**Recommendation:**
Replace `@OmitFromCoverage` with proper unit tests.

Example test structure:
[code block]

Security-critical code should have the highest test coverage, not be omitted.
</details>
```

**Key difference:** Only severity + one-line description visible. All details collapsed.

---

## Summary

**Always use:**
- Minimal summary (verdict + critical issues)
- Separate inline comments with `<details>` tags
- Hybrid emoji + text severity prefixes
- Focus exclusively on actionable feedback

**Never use:**
- Multiple summary sections (Strengths, Recommendations, etc.)
- Praise-only inline comments
- Duplication between summary and inline comments
- Verbose analysis in summary (belongs in inline comments)
