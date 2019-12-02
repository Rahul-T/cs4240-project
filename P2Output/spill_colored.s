.data

.text

main:
sub $sp, $sp, 64
sw $ra, 16($sp)
li $s1, 1
li $s0, 2
li $s2, 3
li $s2, 4
li $s3, 5
li $s3, 6
li $s4, 7
li $s4, 8
li $s5, 9
li $s5, 10
li $s7, 11
li $s5, 0
lw $t5, 28($sp)
add $t5, $s1, $s0
sw $t5, 28($sp)
lw $t7, 28($sp)
add $s6, $t7, $s2
sw $t7, 28($sp)
lw $t5, 44($sp)
add $t5, $s6, $s3
sw $t5, 44($sp)
lw $t7, 44($sp)
add $s6, $t7, $s4
sw $t7, 44($sp)
lw $t5, 60($sp)
add $t5, $s6, $s7
sw $t5, 60($sp)
loop_label0:
bgt $s5, 20,  loop_label1
mul $s6, $s6, $s6
li $t1, 1
add $s5, $s5, $t1
j  loop_label0
loop_label1:
add $s5, $s1, $s0
add $s1, $s5, $s2
add $s0, $s1, $s3
add $s1, $s0, $s4
lw $ra, 16($sp)
addi $sp, $sp, 64
jr $ra
