# CLAUDE.md — Athlete Management System (Java OOP Final Project)

## Project Overview

A console-based Java application for a sports club that manages athletes across three
disciplines (sprinting, swimming, weightlifting) and their coaches. The app loads a starting
roster from a CSV file, lets training sessions be logged against athletes, and runs real
business logic on top of that data: discipline-specific performance scoring, injury-risk
trend detection from training load, and coach-athlete assignment validation.

This is not a CRUD app. The point of the exercise is to demonstrate OOP design (classes,
inheritance, polymorphism, abstraction, encapsulation), SOLID principles, exception handling,
and unit testing — assessed against the course's Java OOP final project brief.

**Due:** 15/11/26 23:59 — source + data file + README on GitHub.

## Project Name

`athlete-management-system`

## Tech Stack

- Java (plain, no framework) — target JDK 17+ for `record`/modern `switch`/`LocalDate` use where useful
- Build: Maven (`pom.xml`) — simplest for a solo, grading-focused project
- Testing: JUnit 5
- Data format: CSV (`athletes.csv`), parsed with plain `BufferedReader`/`String.split` or a tiny hand-rolled parser — no external CSV library needed at this scale
- No GUI, no web framework, no database — console I/O only (satisfies the brief's "simple console application is completely acceptable")

## Architecture

Three-layer separation, enforced to satisfy SRP and avoid a "God class":

- **model/** — domain entities. State + behavior that belongs to the object itself only
  (e.g. an `Athlete` knows how to score itself; it does not know how to rank a roster).
- **service/** — business logic that operates *across* entities (roster assignment,
  performance ranking, injury-risk analysis, scheduling). Services depend on model
  interfaces, not concrete classes, where it matters for DIP.
- **exception/** — custom checked/unchecked exceptions for domain-specific failure cases.
- **utils/** — CSV loading, the console logger.
- **Application.java** — orchestrates: load → build objects → run services → print results.
  Deliberately thin. All real logic lives in services.

## Folder Structure

```
athlete-management-system/
├── pom.xml
├── README.md
├── data/
│   └── athletes.csv
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/club/athletics/
│   │           ├── Application.java
│   │           ├── model/
│   │           │   ├── Person.java              (abstract)
│   │           │   ├── Athlete.java              (abstract)
│   │           │   ├── Sprinter.java
│   │           │   ├── Swimmer.java
│   │           │   ├── Weightlifter.java
│   │           │   ├── Coach.java                (abstract)
│   │           │   ├── SprintCoach.java
│   │           │   ├── SwimCoach.java
│   │           │   ├── WeightliftingCoach.java
│   │           │   ├── TrainingSession.java
│   │           │   └── Discipline.java           (enum)
│   │           ├── capability/
│   │           │   ├── PerformanceScorable.java  (interface)
│   │           │   ├── Trackable.java            (interface)
│   │           │   └── Schedulable.java          (interface — stretch)
│   │           ├── service/
│   │           │   ├── RosterService.java
│   │           │   ├── PerformanceService.java
│   │           │   ├── InjuryRiskService.java
│   │           │   └── SchedulingService.java    (stretch)
│   │           ├── exception/
│   │           │   ├── DisciplineMismatchException.java
│   │           │   ├── InvalidTrainingLoadException.java
│   │           │   ├── AthleteNotFoundException.java
│   │           │   └── SchedulingConflictException.java (stretch)
│   │           └── util/
│   │               ├── CsvAthleteLoader.java
│   │               └── ConsoleLogger.java
│   └── test/
│       └── java/
│           └── com/club/athletics/
│               ├── service/
│               │   ├── RosterServiceTest.java
│               │   ├── PerformanceServiceTest.java
│               │   └── InjuryRiskServiceTest.java
│               └── model/
│                   └── AthleteScoringTest.java
```

## Data Model (`data/athletes.csv`)

Loaded at startup by `CsvAthleteLoader`, not hard-coded. Columns:

```
id,name,dateOfBirth,discipline,disciplineAttribute1,disciplineAttribute2
```

- **Sprinter**: `disciplineAttribute1` = personal best 100m time (seconds), `disciplineAttribute2` = reaction time (seconds)
- **Swimmer**: `disciplineAttribute1` = stroke type, `disciplineAttribute2` = best lap time (seconds)
- **Weightlifter**: `disciplineAttribute1` = weight class (kg), `disciplineAttribute2` = combined snatch + clean & jerk total (kg)

`CsvAthleteLoader` reads the `discipline` column and constructs the correct subclass —
this is the one place that needs to know about all three concrete types; everything
downstream works through the `Athlete` abstraction.

### Data Rules

- A malformed row (wrong column count, non-numeric where a number is expected) is logged
  and skipped — the app must not crash on one bad line, but skipped rows should be visible
  in output, not silently dropped.
- IDs in the CSV must be unique; a duplicate ID is a load-time validation failure.

## Key Class Specifications

### `Person` (abstract)
Shared identity: `id`, `name`, `dateOfBirth`. Exposes `getSummary()` — abstract, each
branch (`Athlete`, `Coach`) formats its own summary differently (this is one of the two
required abstract-class/interface demonstrations, chosen as an abstract class because
`Athlete` and `Coach` share real *state*, not just a capability contract).

### `Athlete` (abstract, extends `Person`)
Adds `coach: Optional<Coach>`, `discipline: Discipline`, `sessions: List<TrainingSession>`.
Implements `Trackable` (`recordSession()`, `getRecentTrainingLoad()`). Declares abstract
`calculatePerformanceScore()` from `PerformanceScorable` — each subclass overrides with its
own simple formula (see Performance Scoring below). This is where polymorphism is
demonstrated: `PerformanceService` iterates a `List<Athlete>` and calls
`calculatePerformanceScore()` without knowing the concrete subclass.

### `Sprinter`, `Swimmer`, `Weightlifter` (extend `Athlete`)
Each adds its own CSV-sourced attributes and overrides `calculatePerformanceScore()`.

### `Coach` (abstract, extends `Person`)
Adds `discipline: Discipline`, `assignedAthletes: List<Athlete>`. Declares
`canSupervise(Athlete)` used by `RosterService` for discipline-match validation.

### `SprintCoach`, `SwimCoach`, `WeightliftingCoach` (extend `Coach`)
Thin subclasses pinning `discipline`; room for discipline-specific coaching notes later if
time allows.

### `TrainingSession`
`athleteId`, `dateTime: LocalDateTime`, `durationMinutes`, `intensity` (1–10 scale).
Immutable once created (no setters) — a session is a historical record. Implements
`Schedulable` for the stretch scheduling feature (`getStartTime()`/`getEndTime()`).

### `Discipline` (enum)
`SPRINTING, SWIMMING, WEIGHTLIFTING` — used everywhere instead of comparing strings;
`RosterService` compares `athlete.getDiscipline() == coach.getDiscipline()`.

## Business Logic Specification

### Performance Scoring (`PerformanceService`, simple formulas — locked scope, no Sinclair/age-grading)

- **Sprinter**: lower PB time + faster reaction time → higher score. Simple inverse-weighted
  formula, e.g. `score = (BASE / pbTime) * (BASE_REACTION / reactionTime)`.
- **Swimmer**: lower lap time → higher score, same inverse-weighted shape.
- **Weightlifter**: higher total lifted relative to weight class → higher score
  (`score = totalLifted / weightClass`).
- `PerformanceService.rankByDiscipline(Discipline)` uses a Stream (`sorted`, `Comparator`)
  to return a leaderboard — this is the earmarked Streams usage.

### Injury-Risk Flagging (`InjuryRiskService`) — the deepest logic in the project

- Computes a rolling training load (`duration × intensity`, summed) over the last 7 days
  per athlete from their `List<TrainingSession>`.
- Compares the most recent 7-day load against the prior 7-day window.
- If the increase exceeds a configurable threshold (e.g. +40%), the athlete is flagged.
- `flagAtRiskAthletes(List<Athlete>)` returns a `List<Athlete>` via Stream filtering —
  deliberately returns real objects, not just printed text, so it can be unit tested
  directly against expected outcomes.

### Roster Assignment (`RosterService`)

- `assignCoach(Athlete, Coach)` — throws `DisciplineMismatchException` if
  `athlete.getDiscipline() != coach.getDiscipline()`.
- `findAthlete(id)` — returns `Optional<Athlete>`; callers that need a non-optional result
  call a variant that throws `AthleteNotFoundException` instead.

### Scheduling Conflict Detection (`SchedulingService`) — STRETCH, build only after core + tests are done

- Detects two `TrainingSession`s for the same athlete whose `[start, end)` windows overlap.
- Throws `SchedulingConflictException` on conflicting session creation rather than silently
  allowing it.
- Not required for the core grade — add only if time remains before the deadline.

## Exception Handling

- `DisciplineMismatchException` — checked, thrown on invalid coach-athlete assignment.
- `InvalidTrainingLoadException` — thrown on malformed session data (negative duration,
  intensity out of 1–10 range).
- `AthleteNotFoundException` — thrown by the non-Optional roster lookup variant.
- `SchedulingConflictException` (stretch) — thrown on overlapping session creation.
- CSV loading uses `try/catch` around per-row parsing so one bad row doesn't abort the load;
  standard `NumberFormatException`/`IOException` are caught and logged meaningfully, never
  swallowed silently.
- Logging goes through a single `ConsoleLogger` utility with one `log()` method that prints
  once — kept intentionally simple; there is no requirement (from the actual course brief)
  for duplicate-printing logger behavior.

## Testing Specification (JUnit 5)

Coverage priorities, in order:

1. `InjuryRiskServiceTest` — normal (no spike), edge (exact threshold boundary), invalid
   (empty session history) cases. This is the richest logic, so it gets the most tests.
2. `PerformanceServiceTest` — one test per discipline's scoring formula, plus a ranking
   order test.
3. `RosterServiceTest` — successful assignment, mismatched-discipline exception case,
   athlete-not-found exception case.
4. `AthleteScoringTest` — polymorphism check: a `List<Athlete>` containing all three
   subclasses, confirming each returns its own score logic.

No getter/setter-only tests — every test exercises a business rule.

## Coding Conventions

- Package root: `com.club.athletics`
- No public field access — every field private, accessed via constructor + purposeful
  methods (not blanket getters/setters where avoidable).
- Services are stateless where possible; they operate on data passed in or held by a
  `RosterService`-owned roster, not on static/global state.
- Favor `Optional<T>` over returning `null` from lookup methods.
- `LocalDate`/`LocalDateTime` for all date/time fields — no raw `String` dates.
- Comments explain *why*, not *what* (e.g. why the threshold for injury risk is 40%, not a
  restatement of the line below it).

## Common Pitfalls to Avoid

- Don't let `Application.java` grow into the God class the brief explicitly warns against —
  it should only wire things together (load → construct → call services → print).
- Don't create inheritance or an interface just to tick a box — `Person`/`Athlete`/`Coach`
  and the capability interfaces all have a genuine reason noted above; be ready to explain
  each one in the README and if asked.
- Don't hard-code the three `Athlete` objects as a shortcut — the CSV load must be the real
  source of initial data, hard-coded objects are explicitly disallowed by the brief.
- Don't catch exceptions and do nothing with them — every catch block logs or rethrows
  meaningfully.
- Don't start `SchedulingService` before the core (model → CSV load → services → tests) is
  complete and passing — it's a stretch goal, not core scope.
