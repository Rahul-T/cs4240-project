.data
r_st_1_0: .space 4

.text

fact_st_1_0:
sub $sp, $sp, 80
sw $ra, 16($sp)
sw $a0, 68($sp)
li $s0, 0
lw $s2, 68($sp)
move $s3, $s2
li $s4, 1
li $s1, 1
bne $s3, $s4,  cond_0_stz_stf_fact_2_0
j  cond_1_after_stf_fact_2_0
cond_0_stz_stf_fact_2_0:
li $s1, 0
cond_1_after_stf_fact_2_0:
beq $s1, $s0,  if_after2__stf_fact_2_0
li $s0, 1
move $v0, $s0
lw $ra, 16($sp)
addi $sp, $sp, 80
jr $ra
if_after2__stf_fact_2_0:
move $s0, $s2
li $s1, 1
sub $s3, $s0, $s1
move $a0, $s3
sw $s0, 72($sp)
sw $s2, 76($sp)
sw $s3, 80($sp)
jal  fact_st_1_0
sw $v0, 20($sp)
lw $s0, 72($sp)
lw $s2, 76($sp)
lw $s3, 80($sp)
lw $s0, 20($sp)
move $s1, $s0
sw $s1, r_st_1_0
move $s0, $s2
move $s2, $s1
mul $s1, $s0, $s2
move $v0, $s1
lw $ra, 16($sp)
addi $sp, $sp, 80
jr $ra

main:
sub $sp, $sp, 40
sw $ra, 16($sp)
li $s0, 1
sw $s0, r_st_1_0
li $s0, 5
move $a0, $s0
sw $s0, 36($sp)
sw $s1, 40($sp)
jal  fact_st_1_0
sw $v0, 24($sp)
lw $s0, 36($sp)
lw $s1, 40($sp)
lw $s1, 24($sp)
move $s0, $s1
sw $s0, r_st_1_0
move $s1, $s0
move $a0, $s1
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 40
jr $ra
