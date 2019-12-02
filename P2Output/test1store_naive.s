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
lw $s1, value
sw $s1, ($t0)
lw $ra, 16($sp)
addi $sp, $sp, 16
jr $ra

main:
sub $sp, $sp, 60
sw $ra, 16($sp)
li $s0,  99
sw $s0, index
li $s0,  9
sw $s0, value
li $s0,  98
sw $s0, 52($sp)
li $s0,  9
sw $s0, 56($sp)
li $t0, 0
Y_init_start:
la $t1,  Y
li $t2, 4
mul $t3, $t0, $t2
add $t1, $t1, $t3
li $t4,  10
sw $t4, ($t1)
addi $t0, 1
blt $t0,  100,  Y_init_start
li $t0, 0
X_init_start:
la $t1,  X
li $t2, 4
mul $t3, $t0, $t2
add $t1, $t1, $t3
li $t4,  10
sw $t4, ($t1)
addi $t0, 1
blt $t0,  100,  X_init_start
li $s0,  0
sw $s0, 36($sp)
li $s0,  0
sw $s0, 20($sp)
sw $s0, 60($sp)
jal  test
lw $s0, 60($sp)
la $t0,  X
li $s0,  99
mulo $s0, $s0, 4
add $t0, $t0, $s0
li $s1,  9
sw $s1, ($t0)
la $t0,  X
lw $s0, 52($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
lw $s1, 56($sp)
sw $s1, ($t0)
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
lw $s1, ($t0)
sw $s1, 24($sp)
la $t0,  Y
lw $s0, 20($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
lw $s1, ($t0)
sw $s1, 28($sp)
lw $s0, 24($sp)
lw $s1, 28($sp)
mul $s2, $s0, $s1
sw $s2, 32($sp)
lw $s0, 36($sp)
lw $s1, 32($sp)
add $s2, $s0, $s1
sw $s2, 40($sp)
lw $s0, 40($sp)
sw $s0, 36($sp)
lw $s0, 20($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 20($sp)
j  loop_label0
loop_label1:
lw $a0, 36($sp)
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 60
jr $ra
