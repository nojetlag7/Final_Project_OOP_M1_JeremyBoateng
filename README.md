# Athlete Management System

A console application for a sports club with sprinters, swimmers and weightlifters. It loads
the club's coaches, athletes and training history from CSV files, then lets staff manage the
roster and analyse performance and injury risk from an interactive menu.

## Purpose

Coaches need to know three things: who is performing best in each discipline, who is training
too hard too suddenly, and which coach can take on which athlete. This application answers
those questions from the club's own data, and enforces the club's assignment and session rules
so the data stays consistent.

## How to run

Requirements: a JDK matching `maven.compiler.source` in `pom.xml`, and Maven (IntelliJ's bundled
Maven works).

```bash
mvn compile exec:java
```

Or run `com.club.athletics.Application` from the IDE.

By default the data comes from `src/main/resources/data/`. To use another data set, pass a
folder containing `athletes.csv`, `coaches.csv` and `sessions.csv`:

```bash
mvn compile exec:java -Dexec.args="path/to/folder"
```

## How to run the tests

```bash
mvn test
```

Or right-click `src/test/java` > *Run 'All Tests'* in IntelliJ.

## Main features

| Menu option | What it does |
|---|---|
| List coaches and athletes | Each person shows their own summary (capacity and certification for coaches; coach and records for athletes) |
| Show athlete details | Performance score and full training history |
| Assign a coach | Lists coaches who could accept the athlete, then validates discipline, capacity and certification |
| Log a training session | Validates duration and intensity (1-10), and rejects sessions that overlap the athlete's existing sessions |
| Show leaderboards | Per-discipline ranking; equal scores share a rank (1, 1, 3) |
| Injury-risk report | Compares each athlete's training load for the last 7 days against the 7 days before, and flags increases above 40% |

Invalid input never crashes the program: typing mistakes are re-prompted, business-rule
violations are reported and the menu continues. Option `0` (or closing the input stream) exits.

## Main classes

| Package | Classes | Responsibility |
|---|---|---|
| `model` | `Person`, `Athlete`, `Sprinter`, `Swimmer`, `Weightlifter`, `Coach`, `SprintCoach`, `SwimCoach`, `WeightliftingCoach`, `TrainingSession`, `Discipline`, `Stroke` | Domain objects and the rules that belong to a single object |
| `capability` | `PerformanceScorable`, `Trackable` | Small interfaces the services depend on |
| `service` | `RosterService`, `RosterInitializer`, `PerformanceService`, `InjuryRiskService`, `SchedulingService`, `RankedEntry`, `LoadComparison` | Business operations across several objects |
| `io` | `DataLoader`, `CsvDataLoader`, `CsvSource`, `ClasspathCsvSource`, `DirectoryCsvSource`, `ClubData` | Loading data |
| `ui` | `ConsoleMenu`, `ConsoleIO`, `MenuAction`, `ui.action.*` | Console interaction |
| `util` | `Logger`, `ConsoleLogger`, `LogLevel` | Logging |
| `exception` | see below | Domain errors |

`Application` only wires these together.

## Technical decisions

1. **Discipline is derived from the class, not stored.** `Athlete.getDiscipline()` is abstract
   and each subclass returns its own value, so a `Swimmer` can never be marked as a sprinter.
2. **Coach rules use a template method.** `Coach.validateAssignment()` fixes the order of the
   shared checks (discipline, then capacity) and calls the abstract `checkSpecialization()` for
   the rule that differs per coach type. The order cannot be broken by a subclass, and adding a
   new coach type only means writing its own rule.
3. **Loaded data is not trusted.** The loader only parses. Assignments and sessions from the CSV
   go through the same `RosterService` methods as console input (`RosterInitializer`), so a data
   file cannot create a state the rules would reject.
4. **Injury risk is computed against an explicit date with integer math.** The report takes an
   `asOf` date (defaulting to the latest session) instead of `LocalDate.now()`, so results and
   tests do not depend on the day they run. The threshold check compares `current * 100` with
   `previous * 140` in integers, so an increase of exactly 40% is never misjudged by
   floating-point rounding. An athlete with no previous load is not flagged, because there is
   no baseline to compare against.
5. **Menu options are command objects.** Each option is a `MenuAction` class, so the menu loop
   never changes when a feature is added, and a failing action is isolated from the loop.

## Inheritance

- `Person` (abstract) > `Athlete` (abstract) > `Sprinter`, `Swimmer`, `Weightlifter`
- `Person` (abstract) > `Coach` (abstract) > `SprintCoach`, `SwimCoach`, `WeightliftingCoach`

`Person` holds the identity shared by everyone (id, name, date of birth). Athletes and coaches
are both people but have different data and behaviour, and each discipline has its own
measurements and rules.

## Polymorphism

- `PerformanceService` ranks a `List<Athlete>` by calling `calculatePerformanceScore()`, and each
  subclass applies its own formula (sprint times, stroke-weighted lap time, lift relative to
  weight class). See `AthleteScoringTest`.
- `RosterService` calls `coach.assign(athlete)` on a `Coach`. The coach type decides the
  specialization rule: any sprinter, certified strokes only, or weight class up to a limit.
- `ListRosterAction` prints a `List<? extends Person>`, and athletes and coaches each produce
  their own `getSummary()`.
- `ConsoleMenu` runs any `MenuAction` without knowing which one it is.

## Abstract classes and interfaces

- **Abstract classes** (`Person`, `Athlete`, `Coach`) are used where subclasses share real
  state and code: identity fields, the session list, the coach-athlete link, the assignment
  template method. A person is never "just" a person, so these classes cannot be instantiated.
- **Interfaces** are used for capabilities that services need, independent of class hierarchy:
  - `PerformanceScorable`: `PerformanceService.rank()` is generic over it, and a test ranks
    lambdas with it.
  - `Trackable`: `InjuryRiskService` only needs a training history, not a full athlete.
  - `DataLoader` / `CsvSource`: the application does not depend on where the data comes from.
    Tests use in-memory strings.
  - `Logger`, `MenuAction`.

## Custom exceptions

All are checked and extend `AthleteManagementException`, so the menu can report any business
error in one place without also catching programming errors.

| Exception | Thrown when |
|---|---|
| `InvalidAssignmentException` | A coach's certification does not cover the athlete (stroke or weight class) |
| `DisciplineMismatchException` (subclass of the above) | Coach and athlete are in different disciplines |
| `CoachCapacityException` (subclass of the above) | The coach already has their maximum number of athletes |
| `AthleteNotFoundException` / `CoachNotFoundException` | An unknown id is used |
| `InvalidTrainingLoadException` | Session duration is not positive or intensity is outside 1-10 |
| `SchedulingConflictException` | A session overlaps another session of the same athlete |
| `DataFormatException` | A CSV row cannot be parsed (columns, number, date, enum, duplicate id) |

Standard exceptions: `IOException` for missing files (fatal, exit code 1),
`IllegalArgumentException` for invalid constructor arguments, `NumberFormatException` and
`DateTimeParseException` (wrapped into `DataFormatException` or re-prompted at the console).
The unchecked `InputClosedException` ends the menu when console input closes.

## How data is loaded

1. `Application` picks a `CsvSource`: the classpath folder `data/` by default, or a folder given
   as an argument.
2. `CsvDataLoader` reads `coaches.csv`, `athletes.csv` and `sessions.csv`. The `discipline`
   column decides which subclass is created. This is the only place that knows the concrete
   types.
3. A bad row is logged with its file and line number and skipped, and loading continues.
   Duplicate ids (across athletes and coaches) are skipped, and the first one wins.
4. `RosterInitializer` applies the `coachId` assignments and attaches sessions through
   `RosterService`. Requests that break a rule are logged and ignored.

```
athletes.csv : id,name,dateOfBirth,discipline,attr1,attr2,coachId
coaches.csv  : id,name,dateOfBirth,discipline,maxAthletes,specialization
sessions.csv : athleteId,dateTime,durationMinutes,intensity
```

| Discipline | attr1 | attr2 | coach specialization |
|---|---|---|---|
| SPRINTING | 100m PB (s) | reaction time (s) | empty |
| SWIMMING | stroke | best 50m lap (s) | certified strokes, `\|`-separated |
| WEIGHTLIFTING | weight class (kg) | total lifted (kg) | max certified weight class (kg) |

Changes made while the program runs are kept in memory only.

## SOLID

- **Single Responsibility:** parsing (`CsvDataLoader`), rule enforcement on startup
  (`RosterInitializer`), ranking (`PerformanceService`), load analysis (`InjuryRiskService`),
  overlap detection (`SchedulingService`), input parsing (`ConsoleIO`), and one class per menu
  option. `Application` only wires them.
- **Open/Closed:** a new coach type only implements `checkSpecialization()`. A new menu option is
  a new `MenuAction`. A new data source is a new `CsvSource` or `DataLoader`. None of these
  require editing existing classes.
- **Liskov Substitution:** every `Athlete` subclass can be scored and tracked through the parent
  type, and every `Coach` subclass keeps the base contract (the discipline and capacity checks
  are `final` in the template method; subclasses can only add a rule).
- **Interface Segregation:** `InjuryRiskService` sees only `Trackable` and `PerformanceService`
  sees only `PerformanceScorable`, not the whole `Athlete` API.
- **Dependency Inversion:** `CsvDataLoader` depends on `CsvSource` and `Logger` abstractions,
  services receive their collaborators through constructors (`RosterService` takes a
  `SchedulingService`), and the console menu depends on `MenuAction`.

## Tests

58 JUnit 5 tests, focused on business rules (no getter-only tests):

| Test class | Covers |
|---|---|
| `CsvDataLoaderTest` | Subclass selection, malformed rows skipped, duplicate ids, invalid sessions, missing file |
| `TrainingSessionTest` | Load calculation, intensity boundaries 1 and 10, invalid 0/11, non-positive duration |
| `CoachAssignmentTest` | Linking, discipline mismatch, capacity, re-assigning to a full coach, stroke and weight-class certification, reassignment, check order |
| `RosterServiceTest` | Lookups, not-found errors, eligible coaches, session logging |
| `RosterInitializerTest` | Invalid CSV assignments and orphan sessions rejected without aborting |
| `PerformanceServiceTest` | Each formula, ranking order, ties, discipline filter, empty input |
| `AthleteScoringTest` | Polymorphic dispatch over a mixed `List<Athlete>` |
| `InjuryRiskServiceTest` | Steady load, exactly 40%, just above, no baseline, empty history, window boundaries |
| `SchedulingServiceTest` | Overlap, contained session, adjacent sessions, different athletes, roster integration |
| `ConsoleMenuTest` | Loop continues after errors, invalid input, exit, end of input |
