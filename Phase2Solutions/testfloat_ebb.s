.data
fp_0: .float 1.0
fp_1: .float 8.0
fp_2: .float 0.0

.text

main:
sub $sp, $sp, 48
sw $ra, 16($sp)
l.s $f6, fp_0
l.s $f7, fp_1
l.s $f8, fp_2
add.s $f8, $f6, $f7
mov.s $f7, $f8
lw $ra, 16($sp)
addi $sp, $sp, 48
jr $ra
