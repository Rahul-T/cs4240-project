.data
acc: .space 4

.text

inc_float:
sub $sp, $sp, 20
sw $ra, 16($sp)
s.s $f12, 20($sp)
l.s $f20, 20($sp)
li.s $f5, 1.0
add.s $f20, $f20, $f5
mov.s $f0, $f20
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
li.s $f20, 0.0
l.s $f21, 20($sp)
l.s $f23, 24($sp)
add.s $f20, $f21, $f23
l.s $f24, 28($sp)
add.s $f20, $f20, $f24
l.s $f22, 32($sp)
add.s $f21, $f20, $f22
s.s $f21, acc
lw $ra, 16($sp)
addi $sp, $sp, 36
jr $ra

main:
sub $sp, $sp, 80
sw $ra, 16($sp)
li.s $f20, 0.0
s.s $f20, acc
li.s $f21, 1.0
li.s $f23, 1.0
li.s $f22, 1.0
li.s $f24, 1.0
li.s $f25, 1.0
li $s1, 0
li $s0, 0
mov.s $f12, $f21
s.s $f20, 52($sp)
s.s $f21, 56($sp)
sw $s1, 60($sp)
s.s $f23, 64($sp)
s.s $f22, 68($sp)
s.s $f24, 72($sp)
sw $s0, 76($sp)
s.s $f25, 80($sp)
jal  inc_float
s.s $f0, 32($sp)
l.s $f20, acc
l.s $f21, 56($sp)
lw $s1, 60($sp)
l.s $f23, 64($sp)
l.s $f22, 68($sp)
l.s $f24, 72($sp)
lw $s0, 76($sp)
l.s $f25, 80($sp)
l.s $f21, 32($sp)
mov.s $f12, $f23
mov.s $f13, $f22
mov.s $f14, $f24
mov.s $f15, $f25
s.s $f20, 52($sp)
s.s $f21, 56($sp)
sw $s1, 60($sp)
s.s $f23, 64($sp)
s.s $f22, 68($sp)
s.s $f24, 72($sp)
sw $s0, 76($sp)
s.s $f25, 80($sp)
jal  sum_floats
l.s $f20, acc
l.s $f21, 56($sp)
lw $s1, 60($sp)
l.s $f23, 64($sp)
l.s $f22, 68($sp)
l.s $f24, 72($sp)
lw $s0, 76($sp)
l.s $f25, 80($sp)
blah: 
li.s $f5, 0.0
c.le.s $f20, $f5
bc1t  conv_inc_start
li.s $f5, 1.0
sub.s $f20, $f20, $f5
s.s $f20, acc
li $t1, 1
add $s0, $s0, $t1
j  blah
conv_inc_start:
li.s $f5, 0.0
c.le.s $f21, $f5
bc1t  conv_inc_end
li.s $f5, 1.0
sub.s $f21, $f21, $f5
li $t1, 1
add $s1, $s1, $t1
j  conv_inc_start
conv_inc_end:
move $a0, $s0
li $v0, 1
syscall
move $a0, $s1
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 80
jr $ra
