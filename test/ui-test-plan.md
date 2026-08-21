# UI Test Plan

## Configuration

- Build command: `javac -d _temp/ui-test-classes src/main/java/*.java`
- Run command: `java -cp _temp/ui-test-classes Soar`
- Working directory: `.`
- Timeout seconds: `10`
- Comparison: `exact after normalizing CRLF and CR line endings to LF`

## Test cases

Add each command/expected-output pair supplied for a testing request here before running `$test-ui`. Every test case must specify:

- a heading in the form `### TC-001 — Short test name`;
- an `- Aim:` line;
- an `#### Inputs` heading followed by a `text` fenced block; and
- an `#### Expected output` heading followed by a `text` fenced block containing the complete expected console output.

There are no test cases yet because no commands or expected outputs have been supplied.
