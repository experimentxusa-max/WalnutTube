WalnutTube — Claude Code Instructions

1. First Priority

Before doing any development work:

1. Read "PROJECT_SPEC.md" completely.
2. Inspect the existing repository.
3. Understand the current architecture and implementation.
4. Identify existing code that can be reused.
5. Follow the project's established conventions unless there is a good reason to change them.

"PROJECT_SPEC.md" is the source of truth for product requirements.

This file defines your development workflow and coding behaviour.

2. Development Philosophy

- Build incrementally.
- Prioritise working functionality over feature quantity.
- Prefer simple, maintainable solutions.
- Avoid unnecessary dependencies.
- Respect the existing architecture.
- Keep guest mode fully functional.
- Prioritise privacy and local-first functionality.

Do not attempt to implement the entire application in one operation.

3. Before Modifying Files

For every significant task:

1. Inspect the relevant files.
2. Explain your proposed approach.
3. Identify affected components.
4. Identify potential risks.
5. Wait for approval when the task involves major architectural changes or destructive operations.

Do not modify files before approval when explicitly instructed to plan only.

4. Coding Standards

- Use Kotlin for application development.
- Use Jetpack Compose for UI.
- Use Coroutines and Flow appropriately.
- Keep business logic outside composables.
- Use ViewModels for screen state.
- Use repositories for data access.
- Use Room for persistent local data.
- Handle loading, success, empty, and error states.
- Avoid duplicated code.
- Use meaningful names.
- Write comments where they clarify non-obvious behaviour.

5. Dependency Management

Before introducing a dependency:

- Check whether an existing dependency already provides the functionality.
- Verify compatibility with the project's Android and Kotlin versions.
- Prefer official documentation and maintained libraries.
- Avoid unnecessary libraries and excessive abstraction.

Do not upgrade unrelated dependencies without justification.

6. Testing and Builds

After meaningful implementation changes:

1. Compile the affected code.
2. Run relevant tests.
3. Fix errors before continuing.
4. Report any remaining warnings or failures.

Never claim that something works without verifying it.

7. Important Project Restrictions

- Do not introduce Shorts.
- Do not add algorithmic recommendations to the default Home feed.
- Do not require Google login for core functionality.
- Do not fabricate video metadata or timestamps.
- Do not pretend local actions are remote YouTube actions.
- Do not treat temporary stream URLs as permanent identifiers.
- Do not bypass DRM or access restrictions.

Follow "PROJECT_SPEC.md" for the complete requirements.

8. Git and File Safety

- Do not delete existing project files without justification.
- Do not overwrite working implementations blindly.
- Avoid destructive Git commands.
- Do not commit or push changes unless explicitly requested.
- Preserve existing user changes.
- Keep changes focused and reviewable.

9. Progress Reporting

At the end of each task, report:

- What was implemented.
- Which files were created or modified.
- Build and test results.
- Known limitations.
- What remains to be done.

Keep the project specification updated when an approved requirement changes.

10. Golden Rule

Understand first. Plan second. Implement third. Test fourth.

A stable, maintainable WalnutTube is more important than rushing to implement every feature.

Mandatory Development Rules

1. Ask before assuming: If requirements, behaviour, or implementation details are unclear, ask me before making assumptions.
2. No placeholder content: Do not use fake data, mock content, dummy implementations, or simulated functionality unless I explicitly approve it.
3. No Lorem Ipsum: All visible text must be meaningful and relevant to the actual application.
4. File tree before code: Before implementing a task, show me the proposed file tree, including files to be created, modified, or deleted. Explain the changes before writing code.
5. No silent decisions: Do not make significant architectural or feature decisions without explaining them.
6. No incomplete claims: Never describe a feature as completed or functional unless it has been implemented and tested.

Remember: Ask when uncertain. Show the plan. Get approval when required. Then implement and verify.