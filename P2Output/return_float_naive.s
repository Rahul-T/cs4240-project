.data
acc: .space 4

.text

inc_float:
sub $sp, $sp, 20
sw $ra, 16($sp)
s.s $f12, 20($sp)
l.s $f20, 20($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 20($sp)
l.s $f0, 20($sp)
lw $ra, 16($sp)
addi $sp, $sp, 20
jr $ra

sum_floats:
sub $sp, $sp, 36
sw $ra, 16($sp)
s.s $f12, 20($sp)
s.s $f13, 24($sp)
s.s $f14, 28($sp)
s.s $f15, 32($sp)
li.s $f20,  0.0
s.s $f20, 36($sp)
l.s $f20, 20($sp)
l.s $f21, 24($sp)
add.s $f22, $f20, $f21
s.s $f22, 36($sp)
l.s $f20, 36($sp)
l.s $f21, 28($sp)
add.s $f22, $f20, $f21
s.s $f22, 36($sp)
l.s $f20, 36($sp)
l.s $f21, 32($sp)
add.s $f22, $f20, $f21
s.s $f22, acc
lw $ra, 16($sp)
addi $sp, $sp, 36
jr $ra

main:
sub $sp, $sp, 56
sw $ra, 16($sp)
li.s $f20,  0.0
s.s $f20, acc
li.s $f20,  1.0
s.s $f20, 32($sp)
li.s $f20,  1.0
s.s $f20, 36($sp)
li.s $f20,  1.0
s.s $f20, 40($sp)
li.s $f20,  1.0
s.s $f20, 44($sp)
li.s $f20,  1.0
s.s $f20, 48($sp)
li $s0,  0
sw $s0, 20($sp)
li $s0,  0
sw $s0, 24($sp)
l.s $f12, 32($sp)
sw $s0, 52($sp)
s.s $f20, 56($sp)
jal  inc_float
s.s $f0, 32($sp)
lw $s0, 52($sp)
l.s $f20, 56($sp)
l.s $f12, 36($sp)
l.s $f13, 40($sp)
l.s $f14, 44($sp)
l.s $f15, 48($sp)
sw $s0, 52($sp)
s.s $f20, 56($sp)
jal  sum_floats
lw $s0, 52($sp)
l.s $f20, 56($sp)
blah: 
l.s $f20, acc
li.s $f21,  0.0
c.le.s $f20, $f21
bc1t  conv_inc_start
l.s $f20, acc
li.s $f21,  1.0
sub.s $f22, $f20, $f21
s.s $f22, acc
lw $s0, 24($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 24($sp)
j  blah
conv_inc_start:
l.s $f20, 32($sp)
li.s $f21,  0.0
c.le.s $f20, $f21
bc1t  conv_inc_end
l.s $f20, 32($sp)
li.s $f21,  1.0
sub.s $f22, $f20, $f21
s.s $f22, 32($sp)
lw $s0, 20($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 20($sp)
j  conv_inc_start
conv_inc_end:
lw $a0, 24($sp)
li $v0, 1
syscall
lw $a0, 20($sp)
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 56
jr $ra
