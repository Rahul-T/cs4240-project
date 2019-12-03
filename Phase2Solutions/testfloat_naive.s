.data
fp_0: .float 1.0
fp_1: .float 8.0
fp_2: .float 0.0

.text

main:
sub $sp, $sp, 48
sw $ra, 16($sp)
l.s $f6, fp_0
s.s $f6, 20($sp)
l.s $f6, fp_1
s.s $f6, 28($sp)
l.s $f6, fp_2
s.s $f6, 36($sp)
l.s $f6, 20($sp)
l.s $f7, 28($sp)
add.s $f8, $f6, $f7
s.s $f8, 44($sp)
l.s $f7, 44($sp)
s.s $f7, 36($sp)
lw $ra, 16($sp)
addi $sp, $sp, 48
jr $ra
