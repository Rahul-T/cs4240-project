.data

.text

main:
sub $sp, $sp, 64
sw $ra, 16($sp)
li $s0,  1
sw $s0, 20($sp)
li $s0,  2
sw $s0, 24($sp)
li $s0,  3
sw $s0, 28($sp)
li $s0,  4
sw $s0, 32($sp)
li $s0,  5
sw $s0, 36($sp)
li $s0,  6
sw $s0, 40($sp)
li $s0,  7
sw $s0, 44($sp)
li $s0,  8
sw $s0, 48($sp)
li $s0,  9
sw $s0, 52($sp)
li $s0,  10
sw $s0, 60($sp)
li $s0,  11
sw $s0, 64($sp)
li $s0,  0
sw $s0, 56($sp)
lw $s0, 20($sp)
lw $s1, 24($sp)
add $s2, $s0, $s1
sw $s2, 28($sp)
lw $s0, 28($sp)
lw $s1, 32($sp)
add $s2, $s0, $s1
sw $s2, 36($sp)
lw $s0, 36($sp)
lw $s1, 40($sp)
add $s2, $s0, $s1
sw $s2, 44($sp)
lw $s0, 44($sp)
lw $s1, 48($sp)
add $s2, $s0, $s1
sw $s2, 52($sp)
lw $s0, 52($sp)
lw $s1, 64($sp)
add $s2, $s0, $s1
sw $s2, 60($sp)
loop_label0:
lw $s0, 56($sp)
li $s1,  20
bgt $s0, $s1,  loop_label1
lw $s0, 52($sp)
lw $s1, 52($sp)
mul $s2, $s0, $s1
sw $s2, 52($sp)
lw $s0, 56($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 56($sp)
j  loop_label0
loop_label1:
lw $s0, 20($sp)
lw $s1, 24($sp)
add $s2, $s0, $s1
sw $s2, 28($sp)
lw $s0, 28($sp)
lw $s1, 32($sp)
add $s2, $s0, $s1
sw $s2, 36($sp)
lw $s0, 36($sp)
lw $s1, 40($sp)
add $s2, $s0, $s1
sw $s2, 44($sp)
lw $s0, 44($sp)
lw $s1, 48($sp)
add $s2, $s0, $s1
sw $s2, 52($sp)
lw $ra, 16($sp)
addi $sp, $sp, 64
jr $ra
