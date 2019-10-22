# cs4240-project
Project for CS 4240: Compilers and Interpreters, Fall 2019


Instructions for building and running the compiler.

## Building The Compiler
### General Commands
To fully build the compiler, run
```
make
```
To remove compiled files and ANTLR generated files, run
```
make clean
```
To rebuild the project (i.e remove compiled and ANTLR-generated files, rebuild the parser, and recompile the compiler), run
```
make rebuild
```

### More Granular Options
To generate the code from ANTLR, run
```
make parser
```

To build the Tiger compiler, run
```
make compiler
```

To remove only compiled files, run
```
make cleanjava
```

## Using the Compiler
To compile a Tiger program (called `<file-name>`) with the built compiler, run
```
java TigerCompiler <file-name> [-no-print] [-no-ir-file]
```

To supress unnecessary output and only print errors, use the `-no-print` flag. The `-no-ir-file` flag will not create a `.ir` file for the compiled program.
