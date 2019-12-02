SPIMPATH=spim-keepstats-master/spim/

make cleanjava
make backend

java TigerBackend cascade.ir >> /dev/null
java TigerBackend factorial.ir >> /dev/null
java TigerBackend float_cascade.ir >> /dev/null
java TigerBackend float_global_param.ir >> /dev/null
java TigerBackend perfect_sqrt.ir >> /dev/null
java TigerBackend sort.ir >> /dev/null
java TigerBackend spill.ir >> /dev/null
java TigerBackend test1.ir >> /dev/null

echo cascade
${SPIMPATH}spim -keepstats -f P2Output/cascade_naive.s 
${SPIMPATH}spim -keepstats -f P2Output/cascade_colored.s 

echo factorial
${SPIMPATH}spim -keepstats -f P2Output/factorial_naive.s 
${SPIMPATH}spim -keepstats -f P2Output/factorial_colored.s 

echo float_cascade
${SPIMPATH}spim -keepstats -f P2Output/float_cascade_naive.s 
${SPIMPATH}spim -keepstats -f P2Output/float_cascade_colored.s

echo float_global_param
${SPIMPATH}spim -keepstats -f P2Output/float_global_param_naive.s 
${SPIMPATH}spim -keepstats -f P2Output/float_global_param_colored.s

echo perfect_sqrt
${SPIMPATH}spim -keepstats -f P2Output/perfect_sqrt_naive.s 
${SPIMPATH}spim -keepstats -f P2Output/perfect_sqrt_colored.s

echo sort
${SPIMPATH}spim -keepstats -f P2Output/sort_naive.s 
${SPIMPATH}spim -keepstats -f P2Output/sort_colored.s 

echo spill
${SPIMPATH}spim -keepstats -f P2Output/spill_naive.s 
${SPIMPATH}spim -keepstats -f P2Output/spill_colored.s 

echo test1
${SPIMPATH}spim -keepstats -f P2Output/test1_naive.s 
${SPIMPATH}spim -keepstats -f P2Output/test1_colored.s 