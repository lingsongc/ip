# UI Test Plan

## Configuration

- Build command: `javac -d _temp/ui-test-classes src/main/java/*.java`
- Run command: `java -cp _temp/ui-test-classes Soar`
- Working directory: `.`
- Timeout seconds: `10`
- Comparison: `exact after normalizing CRLF and CR line endings to LF`

## Test cases

### TC-001 — Polymorphic task types

- Aim: Verify todo, deadline, and event formatting through the shared task list.

#### Inputs

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
deadline do homework /by no idea :-p
mark 2
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] do homework (by: no idea :-p)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][X] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
4.[D][ ] do homework (by: no idea :-p)
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-002 — Informative invalid-input messages

- Aim: Verify that invalid task details, task numbers, and commands identify the problem and let the user continue.

#### Inputs

```text
todo
deadline submit report
event team meeting /from noon
mark two
mark 1
blah
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
The todo description is empty. Give it a few words, and it will be ready to soar!
____________________________________________________________
____________________________________________________________
This deadline has no '/by' date. Add one so it has a clear path through the sky!
____________________________________________________________
____________________________________________________________
This event needs both '/from' and '/to' times to map its flight across the sky!
____________________________________________________________
____________________________________________________________
'two' is not a whole task number. Choose a number from your list to keep flying high!
____________________________________________________________
____________________________________________________________
Your task list is an open sky right now. Add a task before using 'mark'!
____________________________________________________________
____________________________________________________________
That command is on an unfamiliar flight path. Try list, todo, deadline, event, mark, unmark, or bye to keep flying high!
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-003 — Invalid indices preserve completion state

- Aim: Verify that out-of-range mark and unmark commands do not change a valid task's completion state.

#### Inputs

```text
todo read book
mark 2
list
mark 1
unmark 0
list
unmark 1
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Task 2 is outside your list. Choose a number from 1 to 1 and we'll stay on course!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Task 0 is outside your list. Choose a number from 1 to 1 and we'll stay on course!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-004 — Malformed tasks do not enter the list

- Aim: Verify that invalid deadline and event inputs do not change task count, ordering, or existing task data.

#### Inputs

```text
todo anchor task
deadline missing date /by
list
deadline submit report /by Friday
event missing end /from noon /to
list
event team sync /from noon /to 1pm
event /from 2pm /to 3pm
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] anchor task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
The deadline's '/by' date is empty. Add a date or time so it can fly on schedule!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] anchor task
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Friday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
The event's flight times are incomplete. Fill in both '/from' and '/to' values!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] anchor task
2.[D][ ] submit report (by: Friday)
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] team sync (from: noon to: 1pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
The event description is empty. Give it a few words, and it will be ready to soar!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] anchor task
2.[D][ ] submit report (by: Friday)
3.[E][ ] team sync (from: noon to: 1pm)
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-005 — Invalid task-number formats preserve state

- Aim: Verify that missing, negative, decimal, and oversized task numbers do not change a task before or after a valid mark command.

#### Inputs

```text
todo keep state
mark
list
mark -1
list
mark 1.5
list
mark 1
unmark 999999999999999999999
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] keep state
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Add a task number after 'mark' so I know which task should soar next!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep state
____________________________________________________________
____________________________________________________________
Task -1 is outside your list. Choose a number from 1 to 1 and we'll stay on course!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep state
____________________________________________________________
____________________________________________________________
'1.5' is not a whole task number. Choose a number from your list to keep flying high!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep state
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] keep state
____________________________________________________________
____________________________________________________________
'999999999999999999999' is not a whole task number. Choose a number from your list to keep flying high!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] keep state
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-006 — Command lookalikes preserve list state

- Aim: Verify that inputs resembling valid commands are rejected without adding tasks or changing completion state.

#### Inputs

```text
todo real task
todoist fake task
list
mark 1
mark1
list
unmark1
list
unmark 1
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] real task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
That command is on an unfamiliar flight path. Try list, todo, deadline, event, mark, unmark, or bye to keep flying high!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] real task
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] real task
____________________________________________________________
____________________________________________________________
That command is on an unfamiliar flight path. Try list, todo, deadline, event, mark, unmark, or bye to keep flying high!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] real task
____________________________________________________________
____________________________________________________________
That command is on an unfamiliar flight path. Try list, todo, deadline, event, mark, unmark, or bye to keep flying high!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] real task
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] real task
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] real task
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```
