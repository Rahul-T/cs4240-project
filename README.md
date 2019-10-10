# cs4240-project
Project for CS 4240: Compilers and Interpreters, Fall 2019


Instructions for building and running the compiler.

To generate the code from ANTLR, run
```
make parser
```

To build the Tiger compiler, run
```
make compiler
```

To build everything at once, run
```
make
```

To compile a Tiger program with the built compiler, run
```
java TigerCompiler <file-name>
```

To supress unnecessary output and only print errors, use the `-no-print` flag.
