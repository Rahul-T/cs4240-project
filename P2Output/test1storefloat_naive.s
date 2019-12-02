.data
Y: .space 400
index: .space 4
X: .space 400
value: .space 4

.text

test:
sub $sp, $sp, 16
sw $ra, 16($sp)
la $t0,  Y
lw $s0, index
mulo $s0, $s0, 4
add $t0, $t0, $s0
l.s $f20, value
s.s $f20, ($t0)
lw $ra, 16($sp)
addi $sp, $sp, 16
jr $ra

main:
sub $sp, $sp, 64
sw $ra, 16($sp)
li $s0,  99
sw $s0, index
li.s $f20,  9.0
s.s $f20, value
li $s0,  98
sw $s0, 28($sp)
li.s $f20,  9.0
s.s $f20, 56($sp)
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
s.s $f20, 44($sp)
li $s0,  0
sw $s0, 20($sp)
sw $s0, 60($sp)
s.s $f20, 64($sp)
jal  test
lw $s0, 60($sp)
l.s $f20, 64($sp)
la $t0,  X
li $s0,  99
mulo $s0, $s0, 4
add $t0, $t0, $s0
li.s $f20,  9.0
s.s $f20, ($t0)
la $t0,  X
lw $s0, 28($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
l.s $f20, 56($sp)
s.s $f20, ($t0)
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
s.s $f20, 32($sp)
la $t0,  Y
lw $s0, 20($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
l.s $f20, ($t0)
s.s $f20, 36($sp)
l.s $f20, 32($sp)
l.s $f21, 36($sp)
mul.s $f22, $f20, $f21
s.s $f22, 40($sp)
l.s $f20, 44($sp)
l.s $f21, 40($sp)
add.s $f22, $f20, $f21
s.s $f22, 48($sp)
l.s $f20, 48($sp)
s.s $f20, 44($sp)
lw $s0, 20($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 20($sp)
j  loop_label0
loop_label1:
l.s $f12, 44($sp)
li $v0, 2
syscall
lw $ra, 16($sp)
addi $sp, $sp, 64
jr $ra
