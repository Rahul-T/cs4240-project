.data
Y: .space 400
index: .space 4
X: .space 400
value: .space 4

.text

test:
sub $sp, $sp, 16
sw $ra, 16($sp)
lw $s0, index
lw $s1, value
la $t0,  Y
mulo $t1, $s0, 4
add $t0, $t0, $t1
move $s1, $s1
sw $s1, ($t0)
lw $ra, 16($sp)
addi $sp, $sp, 16
jr $ra

main:
sub $sp, $sp, 68
sw $ra, 16($sp)
li $s0, 99
sw $s0, index
li $s0, 9
sw $s0, value
li $s1, 98
li $s0, 9
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
li $s3, 0
li $s2, 0
sw $s1, 60($sp)
sw $s0, 64($sp)
sw $s3, 68($sp)
jal  test
lw $s1, 60($sp)
lw $s0, 64($sp)
lw $s3, 68($sp)
la $t0,  X
li $t2, 99
mulo $t1, $t2, 4
add $t0, $t0, $t1
li $t3, 9
sw $t3, ($t0)
la $t0,  X
mulo $t1, $s1, 4
add $t0, $t0, $t1
move $s0, $s0
sw $s0, ($t0)
li $s1, 0
loop_label0:
bgt $s1, 99,  loop_label1
la $t0,  X
mulo $t1, $s1, 4
add $t0, $t0, $t1
lw $s2, ($t0)
la $t0,  Y
mulo $t1, $s1, 4
add $t0, $t0, $t1
lw $s4, ($t0)
mul $s0, $s2, $s4
add $s2, $s3, $s0
move $s3, $s2
li $t1, 1
add $s1, $s1, $t1
j  loop_label0
loop_label1:
move $a0, $s3
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 68
jr $ra
