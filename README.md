# Sauna-todo

Simple full-stack TODO app example for demonstrating Clojure(script)

## Development

Open terminal, go to the working directory and run:

```bash
lein dev
```

Then open your IDE / editor and start a new REPL from there. Use `(reset)`
to start the server and reset it when you make changes to the backend.

The frontend is running at http://localhost:3000.

The frontend will update automatically when files are saved.

## Tests

```bash
lein alt-test once
lein alt-test auto
```

## Deployment

Open terminal, go to the working directory and run:

```bash
lein prod
```

This will produce an uberjar, which you run with command:

```bash
java -jar target/uberjar/sauna-todo-0.1.0-SNAPSHOT-standalone.jar
```

## License

Copyright © 2017 Metosin

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
