.data

.text

is_sqrt:
sub $sp, $sp, 32
sw $ra, 16($sp)
sw $a0, 32($sp)
li $s0,  1
sw $s0, 20($sp)
li $s0,  0
sw $s0, 24($sp)
begin_loop:
lw $s0, 20($sp)
lw $s1, 20($sp)
mul $s2, $s0, $s1
sw $s2, 28($sp)
lw $s0, 20($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 20($sp)
j  begin_loop
is_true:
li $s0,  1
sw $s0, 24($sp)
exit_loop:
lw $v0, 24($sp)
lw $ra, 16($sp)
addi $sp, $sp, 32
jr $ra

main:
sub $sp, $sp, 28
sw $ra, 16($sp)
lw $a0, 28($sp)
jal  is_sqrt
sw $v0, 24($sp)
lw $a0, 24($sp)
li $v0, 1
syscall
