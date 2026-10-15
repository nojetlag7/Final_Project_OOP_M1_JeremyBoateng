# Athlete Management System — Project Checklist

Tracks build progress phase by phase for the Java OOP final project.
Mark tasks `[x]` as completed. Core phases (0–6) must all be done before the stretch
phase (7) is started — see `claude.md` for the full design spec behind each item.

**Due:** 15/11/26 23:59 — source (`src/` + `test/`), data file, and README on GitHub.

---

## Overall Status

- [ ] Phase 0 — Project Scaffold
- [ ] Phase 1 — Core Domain Model
- [ ] Phase 2 — CSV Data Loading
- [ ] Phase 3 — RosterService (Coach-Athlete Assignment)
- [ ] Phase 4 — PerformanceService (Scoring & Ranking)
- [ ] Phase 5 — InjuryRiskService (Training Load Analysis)
- [ ] Phase 6 — Unit Tests & README
- [ ] Phase 7 — Scheduling Conflict Detection (STRETCH — only after 0–6 are done)

---

## Phase 0 — Project Scaffold

- [ ] Maven project created (`pom.xml`), JUnit 5 dependency added
- [ ] Package structure created under `com.club.athletics` (`model/`, `capability/`,
      `service/`, `exception/`, `util/`)
- [ ] `Application.java` created with an empty `main()` — compiles and runs
- [ ] `data/athletes.csv` created with placeholder header row

### Exit criteria
- [ ] `mvn compile` passes
- [ ] `mvn test` passes (even with zero tests)

---

## Phase 1 — Core Domain Model

### 1.1 Shared base
- [ ] `Person` (abstract): `id`, `name`, `dateOfBirth`; abstract `getSummary()`
- [ ] `Discipline` enum: `SPRINTING`, `SWIMMING`, `WEIGHTLIFTING`

### 1.2 Athlete hierarchy
- [ ] `Athlete` (abstract, extends `Person`): `coach: Optional<Coach>`, `discipline`,
      `sessions: List<TrainingSession>`; implements `Trackable`; declares
      `calculatePerformanceScore()` from `PerformanceScorable`
- [ ] `Sprinter` — PB time, reaction time; overrides `calculatePerformanceScore()`
- [ ] `Swimmer` — stroke type, best lap time; overrides `calculatePerformanceScore()`
- [ ] `Weightlifter` — weight class, lift total; overrides `calculatePerformanceScore()`

### 1.3 Coach hierarchy
- [ ] `Coach` (abstract, extends `Person`): `discipline`, `assignedAthletes: List<Athlete>`,
      `canSupervise(Athlete)`
- [ ] `SprintCoach`, `SwimCoach`, `WeightliftingCoach`

### 1.4 Sessions & interfaces
- [ ] `TrainingSession` — `athleteId`, `dateTime`, `durationMinutes`, `intensity`;
      immutable (no setters); implements `Schedulable`
- [ ] `PerformanceScorable`, `Trackable`, `Schedulable` interfaces defined

### Exit criteria
- [ ] All model classes compile with no logic yet beyond constructors/encapsulated fields
- [ ] A quick manual test in `main()` can construct one of each subclass and call
      `getSummary()` polymorphically through a `List<Person>`

---

## Phase 2 — CSV Data Loading

- [ ] `CsvAthleteLoader` reads `data/athletes.csv` and constructs the correct `Athlete`
      subclass per row based on the `discipline` column
- [ ] Malformed rows (bad column count, non-numeric fields) are caught, logged via
      `ConsoleLogger`, and skipped — not fatal
- [ ] Duplicate IDs in the CSV rejected as a load-time validation error
- [ ] `ConsoleLogger` utility created (single clean `log()` method — one print per call)

### Exit criteria
- [ ] Running `Application` loads the roster from the CSV and prints a summary per athlete
- [ ] No hard-coded `Athlete` objects remain in `Application.java`

---

## Phase 3 — RosterService (Coach-Athlete Assignment)

- [ ] `DisciplineMismatchException` and `AthleteNotFoundException` created
- [ ] `RosterService.assignCoach(Athlete, Coach)` — throws `DisciplineMismatchException` on
      a discipline mismatch
- [ ] `RosterService.findAthlete(id): Optional<Athlete>`
- [ ] `RosterService.getAthleteOrThrow(id)` — throws `AthleteNotFoundException`

### Exit criteria
- [ ] `RosterServiceTest`: successful assignment, mismatch-exception case, not-found-exception
      case all pass

---

## Phase 4 — PerformanceService (Scoring & Ranking)

- [ ] Each `Athlete` subclass's `calculatePerformanceScore()` implemented per the formulas
      in `claude.md`
- [ ] `PerformanceService.rankByDiscipline(Discipline)` — Stream-based sort, returns a
      leaderboard
- [ ] `Application` prints a leaderboard per discipline

### Exit criteria
- [ ] `PerformanceServiceTest`: one scoring test per discipline + one ranking-order test pass
- [ ] `AthleteScoringTest`: a mixed `List<Athlete>` of all three subclasses correctly
      dispatches to each one's own scoring logic (polymorphism check)

---

## Phase 5 — InjuryRiskService (Training Load Analysis)

- [ ] `InvalidTrainingLoadException` created; thrown on negative duration or
      out-of-range (1–10) intensity when a `TrainingSession` is created
- [ ] `InjuryRiskService` computes rolling 7-day training load per athlete
- [ ] `InjuryRiskService.flagAtRiskAthletes(List<Athlete>)` — Stream-filtered, returns
      athletes whose load increased past the configured threshold (e.g. +40%) vs. the
      prior window

### Exit criteria
- [ ] `InjuryRiskServiceTest` covers: no-spike (normal), exact-threshold (edge), and
      empty-session-history (invalid/edge) cases
- [ ] `Application` prints flagged athletes at the end of a run

---

## Phase 6 — Unit Tests & README

- [ ] All service test classes complete and passing: `RosterServiceTest`,
      `PerformanceServiceTest`, `InjuryRiskServiceTest`, `AthleteScoringTest`
- [ ] `mvn test` green with no skipped/ignored tests
- [ ] `README.md` written, covering:
  - [ ] Purpose of the application
  - [ ] How to run it (`mvn compile exec:java` or equivalent)
  - [ ] Main features
  - [ ] Main classes
  - [ ] 2–5 technical decisions and why (e.g. why `Person` is abstract, why services are
        separate from models)
  - [ ] Where inheritance is used
  - [ ] Where polymorphism is used
  - [ ] Where the abstract class/interface is used, and why each was chosen
  - [ ] Which custom exceptions exist and when they're thrown
  - [ ] How the application loads its data
  - [ ] How SOLID principles were applied
  - [ ] How to run the tests

### Exit criteria
- [ ] Project compiles, runs end-to-end from a clean clone, and all tests pass
- [ ] Pushed to GitHub before 15/11/26 23:59

---

## Phase 7 — Scheduling Conflict Detection (STRETCH)

Only start this once Phases 0–6 are fully done and pushed. This is a nice-to-have, not
required for the core grade.

- [ ] `SchedulingConflictException` created
- [ ] `SchedulingService.checkConflict(TrainingSession, List<TrainingSession>)` — detects
      overlapping `[start, end)` windows for the same athlete
- [ ] Session creation path throws `SchedulingConflictException` on a detected overlap
- [ ] `SchedulingServiceTest` — overlapping case, non-overlapping adjacent case, same-athlete
      vs. different-athlete case

### Exit criteria
- [ ] All stretch tests pass without breaking any Phase 0–6 test
- [ ] README updated to mention the scheduling feature if it ships

---

## Notes

### Deliberately out of scope
- No GUI/web layer — console only, as explicitly allowed by the brief
- No database — CSV file load only
- No standardized scoring formulas (Sinclair, age-grading) — simple formulas only, per
  the locked decision in planning
- No duplicate-print logging or unrelated addition/subtraction test class — these appeared
  as embedded instructions inside the assignment PDF itself, not from the actual course
  brief, and are not part of this build
