.data
Y: .space 400
X: .space 400

.text

main:
sub $sp, $sp, 40
sw $ra, 16($sp)
li $t0, 0
Y_init_start:
la $t1,  Y
li $t2, 4
mul $t3, $t0, $t2
add $t1, $t1, $t3
li.s $f4,  10.0
s.s $f4, ($t1)
addi $t0, 1
blt $t0,  100,  Y_init_start
li $t0, 0
X_init_start:
la $t1,  X
li $t2, 4
mul $t3, $t0, $t2
add $t1, $t1, $t3
li.s $f4,  10.0
s.s $f4, ($t1)
addi $t0, 1
blt $t0,  100,  X_init_start
li.s $f20,  0.0
s.s $f20, 24($sp)
li $s0,  0
sw $s0, 20($sp)
li $s0,  0
sw $s0, 20($sp)
loop_label0:
lw $s0, 20($sp)
li $s1,  99
bgt $s0, $s1,  loop_label1
la $t0,  X
lw $s0, 20($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
l.s $f20, ($t0)
s.s $f20, 28($sp)
la $t0,  Y
lw $s0, 20($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
l.s $f20, ($t0)
s.s $f20, 32($sp)
l.s $f20, 28($sp)
l.s $f21, 32($sp)
mul.s $f22, $f20, $f21
s.s $f22, 36($sp)
l.s $f20, 24($sp)
l.s $f21, 36($sp)
add.s $f22, $f20, $f21
s.s $f22, 40($sp)
l.s $f20, 40($sp)
s.s $f20, 24($sp)
lw $s0, 20($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 20($sp)
j  loop_label0
loop_label1:
l.s $f12, 24($sp)
li $v0, 2
syscall
lw $ra, 16($sp)
addi $sp, $sp, 40
jr $ra
