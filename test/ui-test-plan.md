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
