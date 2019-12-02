.data

.text

main:
sub $sp, $sp, 72
sw $ra, 16($sp)
li $s0,  1
sw $s0, 28($sp)
li $s0,  1
sw $s0, 32($sp)
li $s0,  1
sw $s0, 36($sp)
li $s0,  1
sw $s0, 40($sp)
li $s0,  1
sw $s0, 44($sp)
li $s0,  1
sw $s0, 48($sp)
li $s0,  1
sw $s0, 52($sp)
li $s0,  1
sw $s0, 56($sp)
li $s0,  1
sw $s0, 60($sp)
li $s0,  1
sw $s0, 64($sp)
li $s0,  1
sw $s0, 68($sp)
li $s0,  1
sw $s0, 72($sp)
li $s0,  0
sw $s0, 20($sp)
li $s0,  0
sw $s0, 24($sp)
loop_label0:
lw $s0, 20($sp)
li $s1,  30
bgt $s0, $s1,  loop_label1
lw $s0, 28($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 28($sp)
lw $s0, 32($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 32($sp)
lw $s0, 36($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 36($sp)
lw $s0, 40($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 40($sp)
lw $s0, 44($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 44($sp)
lw $s0, 48($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 48($sp)
lw $s0, 52($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 52($sp)
lw $s0, 56($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 56($sp)
lw $s0, 60($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 60($sp)
lw $s0, 64($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 64($sp)
lw $s0, 68($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 68($sp)
lw $s0, 72($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 72($sp)
lw $s0, 20($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 20($sp)
j  loop_label0
loop_label1:
lw $s0, 24($sp)
lw $s1, 28($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $s0, 24($sp)
lw $s1, 32($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $s0, 24($sp)
lw $s1, 36($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $s0, 24($sp)
lw $s1, 40($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $s0, 24($sp)
lw $s1, 44($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $s0, 24($sp)
lw $s1, 48($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $s0, 24($sp)
lw $s1, 52($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $s0, 24($sp)
lw $s1, 56($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $s0, 24($sp)
lw $s1, 60($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $s0, 24($sp)
lw $s1, 64($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $s0, 24($sp)
lw $s1, 68($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $s0, 24($sp)
lw $s1, 72($sp)
add $s2, $s0, $s1
sw $s2, 24($sp)
lw $a0, 24($sp)
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 72
jr $ra
