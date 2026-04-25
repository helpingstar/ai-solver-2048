---
name: planning-android-implementation
version: 0.1.0
description: Architecture design and phased implementation planning for AiSolver2048 Android. Use when planning implementation, designing architecture, creating file inventories, or breaking features into phases. Triggered by "plan implementation", "architecture design", "implementation plan", "break this into phases", "what files do I need", "design the architecture".
---

# Implementation Planning

This skill takes a refined specification (ideally from the `refining-android-requirements` skill) and produces a phased implementation plan with architecture design, file inventory, and risk assessment.

**Prerequisite**: A clear set of requirements. If requirements are vague or incomplete, invoke the `refining-android-requirements` skill first.

---

## Step 1: Classify Change

Determine the change type to guide scope and planning depth:

| Type | Description | Typical Scope |
|------|-------------|---------------|
| **New Feature** | Entirely new functionality, screens, or flows | New files + modifications, multi-phase |
| **Enhancement** | Extending existing feature with new capabilities | Mostly modifications, 1-2 phases |
| **Bug Fix** | Correcting incorrect behavior | Targeted modifications, single phase |
| **Refactoring** | Restructuring without behavior change | Modifications only, migration-aware |
| **Infrastructure** | Build, CI, tooling, or dependency changes | Config files, minimal code changes |

State the classification and rationale before proceeding.

---

## Step 2: Codebase Exploration

Search the codebase to find reference implementations and integration points.

### Find Pattern Anchors

Identify 2-3 existing files that serve as templates for the planned work:

```
**Pattern Anchors:**
1. [file path] — [why this is a good reference]
2. [file path] — [why this is a good reference]
3. [file path] — [why this is a good reference]
```

### Map Integration Points

Identify files that must be modified to integrate the new work:

- **Navigation**: Nav graph registrations, route definitions
- **Dependency Injection**: Hilt modules, `@Provides` / `@Binds` functions
- **Data Layer**: Repository interfaces, data source interfaces
- **Feature Flags**: Feature flag definitions and checks
- **Managers**: Single-responsibility data layer classes (see `docs/ARCHITECTURE.md` Managers section)

### Document Existing Patterns

Note the specific patterns used by the pattern anchors:
- State class structure (sealed class, data class fields)
- Action/Event naming conventions
- Repository method signatures and return types
- Test structure and assertion patterns

---

## Step 3: Architecture Design

Produce an ASCII diagram showing component relationships for the planned work:

```
┌─────────────────┐
│   Screen        │ ← Compose UI
│  (Composable)   │
└────────┬────────┘
         │ State / Action / Event
┌────────▼────────┐
│   ViewModel     │ ← Business logic orchestration
└────────┬────────┘
         │ Repository calls
┌────────▼────────┐
│   Repository    │ ← Data coordination (sealed class results)
└───┬────┬────┬───┘
    │    │    │
┌───▼───┐ │ ┌─▼──────┐
│Manager│ │ │Manager │ ← Single-responsibility (optional)
└───┬───┘ │ └─┬──────┘
    │     │   │
┌───▼─────▼───▼────┐
│   Data Sources   │ ← Raw data (Result<T>, never throw)
└─┬────┬───────────┘
  │    │
SharedPrefs LiteRT
```

Adapt the diagram to show the actual components planned. _Consult `docs/ARCHITECTURE.md` for full data layer patterns and conventions._

### Design Decisions

Document key architectural decisions in a table:

| Decision | Resolution | Rationale |
|----------|-----------|-----------|
| [What needed deciding] | [What was chosen] | [Why] |

---

## Step 4: File Inventory

### Files to Create

| File Path | Type | Pattern Reference |
|-----------|------|-------------------|
| [full path] | [ViewModel / Screen / Repository / etc.] | [pattern anchor file] |

**Include in file inventory:**
- `...Navigation.kt` files for new screens
- `...Module.kt` Hilt module files for new DI bindings
- Paired test files (`...Test.kt`) for each new class

### Files to Modify

| File Path | Change Description | Risk Level |
|-----------|-------------------|------------|
| [full path] | [what changes] | Low / Medium / High |

**Risk levels:**
- **Low**: Additive changes (new entries in nav graph, new bindings in Hilt module)
- **Medium**: Modifying existing logic (adding parameters, new branches)
- **High**: Changing interfaces, data models, or shared utilities

---

## Step 5: Implementation Phases

Break the work into sequential phases. Each phase should be independently testable and committable.

**Phase ordering principle**: Foundation → Data → UI

For each phase:

```markdown
### Phase N: [Name]

**Goal**: [What this phase accomplishes]

**Files**:
- Create: [list]
- Modify: [list]

**Tasks**:
1. [Specific implementation task]
2. [Specific implementation task]
3. ...

**Verification**:
- [Test command or manual verification step]

**Skills**: [Which workflow skills apply — e.g., `implementing-android-code`]
```

### Phase Guidelines

- Each phase should be small enough to be independently committable
- UI phases come after their data dependencies are in place
- If a phase has more than 5 tasks, consider splitting it

---

## Step 6: Risk & Verification

### Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| [What could go wrong] | Low/Med/High | Low/Med/High | [How to prevent or handle] |

### Verification Plan

**Manual Verification:**
- [Specific manual test scenarios]
- [Edge cases to manually verify]
- Verify ViewModel state survives process death (test via `SavedStateHandle` persistence and `Don't keep activities` developer option)
