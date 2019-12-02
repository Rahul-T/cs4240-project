.data
t4: .space 4
t5: .space 4
t6: .space 4
t7: .space 4
t8: .space 4
t9: .space 4
t10: .space 4
t12: .space 4
t11: .space 4
t14: .space 4
diag1: .space 60
t13: .space 4
t16: .space 4
X: .space 4
t15: .space 4
t18: .space 4
t17: .space 4
t19: .space 4
res: .space 4
diag2: .space 60
r: .space 4
row: .space 32
t1: .space 4
t2: .space 4
col: .space 32
t3: .space 4

.text

try:
sub $sp, $sp, 32
sw $ra, 16($sp)
sw $a0, 20($sp)
li $s0,  0
sw $s0, r
label1:
lw $s0, r
li $s1,  7
bgt $s0, $s1,  label2
la $t0,  row
lw $s0, r
mulo $s0, $s0, 4
add $t0, $t0, $s0
lw $s1, ($t0)
sw $s1, t1
lw $s0, t1
li $s1,  0
bne $s0, $s1,  label3
li $s0,  1
li $s1,  0
add $s2, $s0, $s1
sw $s2, t2
j  label4
label3:
li $s0,  0
li $s1,  0
add $s2, $s0, $s1
sw $s2, t2
label4:
lw $s0, r
lw $s1, 20($sp)
add $s2, $s0, $s1
sw $s2, t3
la $t0,  diag1
lw $s0, t3
mulo $s0, $s0, 4
add $t0, $t0, $s0
lw $s1, ($t0)
sw $s1, t4
lw $s0, t4
li $s1,  0
bne $s0, $s1,  label5
li $s0,  1
li $s1,  0
add $s2, $s0, $s1
sw $s2, t5
j  label6
label5:
li $s0,  0
li $s1,  0
add $s2, $s0, $s1
sw $s2, t5
label6:
lw $s0, r
li $s1,  7
add $s2, $s0, $s1
sw $s2, t6
lw $s0, t6
lw $s1, 20($sp)
sub $s2, $s0, $s1
sw $s2, t7
la $t0,  diag2
lw $s0, t7
mulo $s0, $s0, 4
add $t0, $t0, $s0
lw $s1, ($t0)
sw $s1, t8
lw $s0, t8
li $s1,  0
bne $s0, $s1,  label7
li $s0,  1
li $s1,  0
add $s2, $s0, $s1
sw $s2, t9
j  label8
label7:
li $s0,  0
li $s1,  0
add $s2, $s0, $s1
sw $s2, t9
label8:
lw $s0, t5
lw $s1, t9
and $s2, $s0, $s1
sw $s2, t10
lw $s0, t2
lw $s1, t10
and $s2, $s0, $s1
sw $s2, t11
lw $s0, t11
li $s1,  0
beq $s0, $s1,  label9
lw $s0, res
li $s1,  1
add $s2, $s0, $s1
sw $s2, t12
lw $s0, t12
sw $s0, res
lw $s0, r
lw $s1, 20($sp)
add $s2, $s0, $s1
sw $s2, t13
lw $s0, r
li $s1,  7
add $s2, $s0, $s1
sw $s2, t14
lw $s0, t13
lw $s1, 20($sp)
sub $s2, $s0, $s1
sw $s2, t15
lw $s0, 20($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, t16
sw $s0, 24($sp)
sw $s1, 28($sp)
sw $s2, 32($sp)
jal  try
sw $v0, X
lw $s0, 24($sp)
lw $s1, 28($sp)
lw $s2, 32($sp)
lw $s0, r
lw $s1, 20($sp)
add $s2, $s0, $s1
sw $s2, t17
lw $s0, r
li $s1,  7
add $s2, $s0, $s1
sw $s2, t18
lw $s0, t17
lw $s1, 20($sp)
sub $s2, $s0, $s1
sw $s2, t19
j  label10
label9:
label10:
lw $s0, r
li $s1,  1
add $s2, $s0, $s1
sw $s2, r
j  label1
label2:
lw $v0, res
lw $ra, 16($sp)
addi $sp, $sp, 32
jr $ra

main:
sub $sp, $sp, 104
sw $ra, 16($sp)
li $t0, 0
diag1_init_start:
la $t1,  diag1
li $t2, 4
mul $t3, $t0, $t2
add $t1, $t1, $t3
li $t4,  0
sw $t4, ($t1)
addi $t0, 1
ble $t0,  15,  diag1_init_start
li $t0, 0
diag2_init_start:
la $t1,  diag2
li $t2, 4
mul $t3, $t0, $t2
add $t1, $t1, $t3
li $t4,  0
sw $t4, ($t1)
addi $t0, 1
ble $t0,  15,  diag2_init_start
li $a0, 0
jal  try
sw $v0, X
lw $a0, X
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 104
jr $ra
