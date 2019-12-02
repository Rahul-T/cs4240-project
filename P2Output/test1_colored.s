.data
Y: .space 400
X: .space 400

.text

main:
sub $sp, $sp, 40
sw $ra, 16($sp)
li $t0, 0
Y_init_start:
la $t1,  Y
li $t2, 4
mul $t3, $t0, $t2
add $t1, $t1, $t3
li $t4,  10
sw $t4, ($t1)
addi $t0, 1
ble $t0,  100,  Y_init_start
li $t0, 0
X_init_start:
la $t1,  X
li $t2, 4
mul $t3, $t0, $t2
add $t1, $t1, $t3
li $t4,  10
sw $t4, ($t1)
addi $t0, 1
ble $t0,  100,  X_init_start
li $s3, 0
li $s0, 0
li $s2, 0
loop_label0:
bgt $s2, 99,  loop_label1
la $t0,  X
mulo $t1, $s2, 4
add $t0, $t0, $t1
lw $s1, ($t0)
la $t0,  Y
mulo $t1, $s2, 4
add $t0, $t0, $t1
lw $s4, ($t0)
mul $s0, $s1, $s4
add $s1, $s3, $s0
move $s3, $s1
li $t1, 1
add $s2, $s2, $t1
j  loop_label0
loop_label1:
move $a0, $s3
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 40
jr $ra
