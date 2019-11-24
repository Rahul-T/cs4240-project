.data
r_st_1_0: .space 4

.text

fact_st_1_0:
sub $sp, $sp, 80
sw $ra, 16($sp)
sw $a0, 68($sp)
li $s0,  0
sw $s0, 48($sp)
lw $s0, 68($sp)
sw $s0, 36($sp)
li $s0,  1
sw $s0, 40($sp)
li $s0,  1
sw $s0, 44($sp)
lw $s0, 36($sp)
lw $s1, 40($sp)
bne $s0, $s1,  cond_0_stz_stf_fact_2_0
j  cond_1_after_stf_fact_2_0
cond_0_stz_stf_fact_2_0:
li $s0, 0
sw $s0, 44($sp)
cond_1_after_stf_fact_2_0:
lw $s0, 44($sp)
lw $s1, 48($sp)
beq $s0, $s1,  if_after2__stf_fact_2_0
li $s0,  1
sw $s0, 32($sp)
lw $v0, 32($sp)
lw $ra, 16($sp)
addi $sp, $sp, 80
jr $ra
if_after2__stf_fact_2_0:
lw $s0, 68($sp)
sw $s0, 56($sp)
li $s0,  1
sw $s0, 60($sp)
lw $s0, 56($sp)
lw $s1, 60($sp)
sub $s2, $s0, $s1
sw $s2, 64($sp)
lw $a0, 64($sp)
sw $s0, 72($sp)
sw $s1, 76($sp)
sw $s2, 80($sp)
jal  fact_st_1_0
sw $v0, 20($sp)
lw $s0, 72($sp)
lw $s1, 76($sp)
lw $s2, 80($sp)
lw $s0, 20($sp)
sw $s0, r_st_1_0
lw $s0, 68($sp)
sw $s0, 24($sp)
lw $s0, r_st_1_0
sw $s0, 28($sp)
lw $s0, 24($sp)
lw $s1, 28($sp)
mul $s2, $s0, $s1
sw $s2, 52($sp)
lw $v0, 52($sp)
lw $ra, 16($sp)
addi $sp, $sp, 80
jr $ra

main:
sub $sp, $sp, 36
sw $ra, 16($sp)
li $s0,  1
sw $s0, 32($sp)
li $s0,  5
sw $s0, 20($sp)
lw $a0, 20($sp)
sw $s0, 36($sp)
jal  fact_st_1_0
sw $v0, 24($sp)
lw $s0, 36($sp)
lw $s0, 24($sp)
sw $s0, 32($sp)
lw $s0, 32($sp)
sw $s0, 28($sp)
lw $a0, 28($sp)
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 36
jr $ra
