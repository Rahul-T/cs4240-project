.data

.text

main:
sub $sp, $sp, 96
sw $ra, 16($sp)
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
li.s $f20,  1.0
s.s $f20, 52($sp)
li.s $f20,  1.0
s.s $f20, 56($sp)
li.s $f20,  1.0
s.s $f20, 60($sp)
li.s $f20,  1.0
s.s $f20, 64($sp)
li.s $f20,  1.0
s.s $f20, 68($sp)
li.s $f20,  1.0
s.s $f20, 72($sp)
li.s $f20,  1.0
s.s $f20, 76($sp)
li.s $f20,  1.0
s.s $f20, 80($sp)
li.s $f20,  1.0
s.s $f20, 84($sp)
li.s $f20,  1.0
s.s $f20, 88($sp)
li.s $f20,  1.0
s.s $f20, 92($sp)
li.s $f20,  1.0
s.s $f20, 96($sp)
li.s $f20,  0.0
s.s $f20, 28($sp)
li $s0,  0
sw $s0, 24($sp)
li $s0,  0
sw $s0, 20($sp)
loop_label0:
lw $s0, 24($sp)
li $s1,  30
bgt $s0, $s1,  loop_label1
l.s $f20, 32($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 32($sp)
l.s $f20, 36($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 36($sp)
l.s $f20, 40($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 40($sp)
l.s $f20, 44($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 44($sp)
l.s $f20, 48($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 48($sp)
l.s $f20, 52($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 52($sp)
l.s $f20, 56($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 56($sp)
l.s $f20, 60($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 60($sp)
l.s $f20, 64($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 64($sp)
l.s $f20, 68($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 68($sp)
l.s $f20, 72($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 72($sp)
l.s $f20, 76($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 76($sp)
l.s $f20, 80($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 80($sp)
l.s $f20, 84($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 84($sp)
l.s $f20, 88($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 88($sp)
l.s $f20, 92($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 92($sp)
l.s $f20, 96($sp)
li.s $f21,  1.0
add.s $f22, $f20, $f21
s.s $f22, 96($sp)
lw $s0, 24($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 24($sp)
j  loop_label0
loop_label1:
l.s $f20, 28($sp)
l.s $f21, 32($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 36($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 40($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 44($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 48($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 52($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 56($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 60($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 64($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 68($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 72($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 76($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 84($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 80($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 88($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 92($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
l.s $f20, 28($sp)
l.s $f21, 96($sp)
add.s $f22, $f20, $f21
s.s $f22, 28($sp)
loop_label2:
l.s $f20, 28($sp)
li.s $f21,  0.0
c.lt.s $f20, $f21
bc1t  loop_label3
l.s $f20, 28($sp)
li.s $f21,  1.0
sub.s $f22, $f20, $f21
s.s $f22, 28($sp)
lw $s0, 20($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 20($sp)
j  loop_label2
loop_label3:
lw $a0, 20($sp)
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 96
jr $ra
