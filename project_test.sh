make cleanjava
make backend

java TigerBackend cascade.ir >> ../junk.txt
java TigerBackend factorial.ir >> ../junk.txt
java TigerBackend float_cascade.ir >> ../junk.txt
java TigerBackend float_global_param.ir >> ../junk.txt
# java TigerBackend nqueens.ir >> ../junk.txt
java TigerBackend perfect_sqrt.ir >> ../junk.txt
java TigerBackend sort.ir >> ../junk.txt
java TigerBackend spill.ir >> ../junk.txt
java TigerBackend test1.ir >> ../junk.txt

echo cascade
$SPIMPATH -keepstats -f P2Output/cascade_naive.s 
$SPIMPATH -keepstats -f P2Output/cascade_colored.s 

echo factorial
$SPIMPATH -keepstats -f P2Output/factorial_naive.s 
$SPIMPATH -keepstats -f P2Output/factorial_colored.s 

echo float_cascade
$SPIMPATH -keepstats -f P2Output/float_cascade_naive.s 
$SPIMPATH -keepstats -f P2Output/float_cascade_colored.s

echo float_global_param
$SPIMPATH -keepstats -f P2Output/float_global_param_naive.s 
$SPIMPATH -keepstats -f P2Output/float_global_param_colored.s

# echo nqueens
# $SPIMPATH -keepstats -f P2Output/nqueens_naive.s 
# $SPIMPATH -keepstats -f P2Output/nqueens_colored.s 

echo perfect_sqrt
$SPIMPATH -keepstats -f P2Output/perfect_sqrt_naive.s 
$SPIMPATH -keepstats -f P2Output/perfect_sqrt_colored.s

echo sort
$SPIMPATH -keepstats -f P2Output/sort_naive.s 
$SPIMPATH -keepstats -f P2Output/sort_colored.s 

echo spill
$SPIMPATH -keepstats -f P2Output/spill_naive.s 
$SPIMPATH -keepstats -f P2Output/spill_colored.s 

echo test1
$SPIMPATH -keepstats -f P2Output/test1_naive.s 
$SPIMPATH -keepstats -f P2Output/test1_colored.s 