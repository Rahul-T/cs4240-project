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
sub $sp, $sp, 40
sw $ra, 16($sp)
sw $a0, 20($sp)
lw $s2, res
li $s1, 0
sw $s1, r
label1:
bgt $s1, 7,  label2
la $t0,  row
mulo $t1, $s1, 4
add $t0, $t0, $t1
lw $s3, ($t0)
sw $s3, t1
bne $s3, 0,  label3
li $t0, 1
li $t1, 0
add $s3, $t0, $t1
sw $s3, t2
j  label4
label3:
li $t0, 0
li $t1, 0
add $s3, $t0, $t1
sw $s3, t2
label4:
lw $s0, 20($sp)
add $s5, $s1, $s0
sw $s5, t3
la $t0,  diag1
mulo $t1, $s5, 4
add $t0, $t0, $t1
lw $s4, ($t0)
sw $s4, t4
bne $s4, 0,  label5
li $t0, 1
li $t1, 0
add $s4, $t0, $t1
sw $s4, t5
j  label6
label5:
li $t0, 0
li $t1, 0
add $s4, $t0, $t1
sw $s4, t5
label6:
li $t1, 7
add $s5, $s1, $t1
sw $s5, t6
sub $s6, $s5, $s0
sw $s6, t7
la $t0,  diag2
mulo $t1, $s6, 4
add $t0, $t0, $t1
lw $s5, ($t0)
sw $s5, t8
bne $s5, 0,  label7
li $t0, 1
li $t1, 0
add $s5, $t0, $t1
sw $s5, t9
j  label8
label7:
li $t0, 0
li $t1, 0
add $s5, $t0, $t1
sw $s5, t9
label8:
and $s6, $s4, $s5
sw $s6, t10
and $s4, $s3, $s6
sw $s4, t11
beq $s4, 0,  label9
li $t1, 1
add $s3, $s2, $t1
sw $s3, t12
move $s2, $s3
sw $s2, res
add $s3, $s1, $s0
sw $s3, t13
li $t1, 7
add $s4, $s1, $t1
sw $s4, t14
sub $s4, $s3, $s0
sw $s4, t15
li $t1, 1
add $s3, $s0, $t1
sw $s3, t16
sw $s2, 24($sp)
sw $s1, 28($sp)
sw $s0, 32($sp)
sw $s3, 36($sp)
sw $s4, 40($sp)
jal  try
sw $v0, X
lw $s2, res
lw $s1, r
lw $s0, 32($sp)
lw $s3, t16
lw $s4, X
lw $s4, X
add $s3, $s1, $s0
sw $s3, t17
li $t1, 7
add $s4, $s1, $t1
sw $s4, t18
sub $s4, $s3, $s0
sw $s4, t19
j  label10
label9:
label10:
li $t1, 1
add $s1, $s1, $t1
sw $s1, r
j  label1
label2:
move $v0, $s2
lw $ra, 16($sp)
addi $sp, $sp, 40
jr $ra

main:
sub $sp, $sp, 108
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
sw $s0, 108($sp)
jal  try
sw $v0, X
lw $s0, X
lw $s0, X
lw $a0, X
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 108
jr $ra
