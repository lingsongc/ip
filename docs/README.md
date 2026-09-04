# Soar User Guide

Soar is a task-tracking chatbot. Enter commands in the chat box to add, list, find, complete, delete,
or edit tasks.

## Editing tasks

Use `edit` to change one or more details without deleting and recreating a task:

```text
edit TASK_NUMBER FIELD VALUE [FIELD VALUE ...]
```

The task number is the number shown by `list`. Fields may appear in any order, but each field may
appear only once in a command.

| Task type | Editable fields |
|---|---|
| Todo | `/description` |
| Deadline | `/description`, `/by` |
| Event | `/description`, `/from`, `/to` |

Examples:

```text
edit 1 /description read two chapters
edit 2 /description submit final report /by 20 Sep 2026 6:00 PM
edit 3 /to 5pm /from 3pm
```

An edit preserves the task's type, completion status, list position, and every field not included in
the command. If any supplied field is invalid, none of the fields are changed.

Soar shows both versions after a successful edit:

```text
I've updated this task:
  Before: [E][ ] meeting (from: 2pm to: 4pm)
  After:  [E][ ] meeting (from: 2pm to: 5pm)
```

### Field-marker restriction

Every standalone slash-word token is treated as the start of another field. Consequently, values
cannot contain standalone tokens such as `/to`, `/by`, or `/notes` as literal text. Unknown field
markers are rejected.

### Dates and event times

Deadline `/by` values accept these formats:

```text
yyyy-MM-dd
d/M/yyyy
d/M/yyyy HHmm
yyyy-MM-dd HH:mm
d MMM yyyy h:mm a
ISO local date-time
```

Event `/from` and `/to` values remain free-form and only need to be nonblank. If an event endpoint
contains a supported date or date-time, Soar can include that event in matching `date` results.

### Stored data

Soar stores deadlines in canonical ISO date or ISO local date-time form. It does not migrate
malformed legacy deadline values such as `tmr`; invalid stored records must be removed or repaired
before Soar can start safely.
