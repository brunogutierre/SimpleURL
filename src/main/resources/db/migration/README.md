# Database migrations

These scripts document the target relational schema (PostgreSQL dialect) in
[Flyway](https://documentation.red-gate.com/flyway) naming format.

They are **not executed**: the application currently runs on in-memory
repositories. The JPA entities in the source code map exactly these tables, so
switching to a real database only requires adding a JPA provider, a JDBC driver
and Flyway, which will then apply these scripts unchanged.

Rules: never edit a script that has been released; add a new versioned script instead.
