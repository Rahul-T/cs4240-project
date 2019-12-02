.data

.text

main:
sub $sp, $sp, 72
sw $ra, 16($sp)
li $s6, 1
li $s3, 1
lw $t7, 36($sp)
li $t7, 1
sw $t7, 36($sp)
li $s7, 1
li $s0, 1
li $s2, 1
li $s4, 1
li $s1, 1
lw $t7, 60($sp)
li $t7, 1
sw $t7, 60($sp)
li $s5, 1
lw $t7, 68($sp)
li $t7, 1
sw $t7, 68($sp)
lw $t7, 72($sp)
li $t7, 1
sw $t7, 72($sp)
lw $t7, 20($sp)
li $t7, 0
sw $t7, 20($sp)
lw $t7, 24($sp)
li $t7, 0
sw $t7, 24($sp)
loop_label0:
lw $t7, 20($sp)
bgt $t7, 30,  loop_label1
sw $t7, 20($sp)
li $t1, 1
add $s6, $s6, $t1
li $t1, 1
add $s3, $s3, $t1
lw $t7, 36($sp)
lw $t5, 36($sp)
li $t1, 1
add $t5, $t7, $t1
sw $t7, 36($sp)
sw $t5, 36($sp)
li $t1, 1
add $s7, $s7, $t1
li $t1, 1
add $s0, $s0, $t1
li $t1, 1
add $s2, $s2, $t1
li $t1, 1
add $s4, $s4, $t1
li $t1, 1
add $s1, $s1, $t1
lw $t7, 60($sp)
lw $t5, 60($sp)
li $t1, 1
add $t5, $t7, $t1
sw $t7, 60($sp)
sw $t5, 60($sp)
li $t1, 1
add $s5, $s5, $t1
lw $t7, 68($sp)
lw $t5, 68($sp)
li $t1, 1
add $t5, $t7, $t1
sw $t7, 68($sp)
sw $t5, 68($sp)
lw $t7, 72($sp)
lw $t5, 72($sp)
li $t1, 1
add $t5, $t7, $t1
sw $t7, 72($sp)
sw $t5, 72($sp)
lw $t7, 20($sp)
lw $t5, 20($sp)
li $t1, 1
add $t5, $t7, $t1
sw $t7, 20($sp)
sw $t5, 20($sp)
j  loop_label0
loop_label1:
lw $t7, 24($sp)
lw $t5, 24($sp)
add $t5, $t7, $s6
sw $t7, 24($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
lw $t5, 24($sp)
add $t5, $t7, $s3
sw $t7, 24($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
lw $t6, 36($sp)
lw $t5, 24($sp)
add $t5, $t7, $t6
sw $t7, 24($sp)
sw $t6, 36($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
lw $t5, 24($sp)
add $t5, $t7, $s7
sw $t7, 24($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
lw $t5, 24($sp)
add $t5, $t7, $s0
sw $t7, 24($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
lw $t5, 24($sp)
add $t5, $t7, $s2
sw $t7, 24($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
lw $t5, 24($sp)
add $t5, $t7, $s4
sw $t7, 24($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
lw $t5, 24($sp)
add $t5, $t7, $s1
sw $t7, 24($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
lw $t6, 60($sp)
lw $t5, 24($sp)
add $t5, $t7, $t6
sw $t7, 24($sp)
sw $t6, 60($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
lw $t5, 24($sp)
add $t5, $t7, $s5
sw $t7, 24($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
lw $t6, 68($sp)
lw $t5, 24($sp)
add $t5, $t7, $t6
sw $t7, 24($sp)
sw $t6, 68($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
lw $t6, 72($sp)
lw $t5, 24($sp)
add $t5, $t7, $t6
sw $t7, 24($sp)
sw $t6, 72($sp)
sw $t5, 24($sp)
lw $t7, 24($sp)
move $a0, $t7
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 72
jr $ra
